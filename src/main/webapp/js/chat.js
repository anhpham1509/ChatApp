$(document).ready(function () {

    var token = localStorage.getItem("token");
    var email = localStorage.getItem('email');
    var currentTarget = "";
    var joinedGroups = [];

    getToken();

    //Insert Username
    $('#username').html(email);

    feedMessage();
    listJoinedGroups();
    listAllUsers();
    addChannel();

    // Polling messages
    function feedMessage() {
        $.ajax({
            url: "/ChatApp/app/chat",
            method: "GET",
            dataType: "xml",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (result) {
                // Handle new mess
                updateNewMess(result);
                feedMessage();
            },
            error: function () {
                feedMessage();
            }
        });
    }

    // Do Ajax
    function callAjax(url, method, data, contentType, callback) {
        $.ajax({
            url: url,
            method: method,
            contentType: contentType,
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (result) {
                callback(result);
            }
        });
    }

    // Show All Users
    function listAllUsers() {
        callAjax("/ChatApp/app/user/", "GET", null, "application/xml", updateUser);
    }

    // List all users in ul
    function updateUser(data) {
        $("ul#users").html(" ");
        $(data).find("email").each(function () {
            if (this.innerHTML !== email) {
                $("ul#users").append("<li><a href='#" + this.innerHTML + "' class='user'>" + this.innerHTML + "</a></li>");
            }
        });

        //getPrivateHistory($('ul#users li:nth-child(2)').text());

        $('.user').click(function () {
            getPrivateHistory($(this).html());
        });
    }

    // Show Groups Users joined
    function listJoinedGroups() {
        callAjax("/ChatApp/app/group/", "GET", null, "application/xml", updateGroup);
    }


    // List all joined groups in ul
    function updateGroup(data) {
        $("ul#channels").html(" ");
        $(data).find("name").each(function () {
            joinedGroups.push(this.innerHTML);
            $("ul#channels").append("<li><a href='#" + this.innerHTML + "' class='channel'>" + this.innerHTML + "</a></li>");
        });
        //console.log(joinedGroups);
        $('ul#channels').append("\
            <li>\
                <a id='join-modal' href='javascript:;' data-toggle='modal'\
                        data-target='#join-channel'>Join new channel</a>\
            </li>");

        //getGroupHistory($('ul#channels li:nth-child(1)').text());


        $('.channel').click(function () {
            getGroupHistory($(this).html());
        });
        listAllGroup();
    }

    // Check joined groups
    function checkJoined(group) {
        return joinedGroups.some(function(elem) {
            //console.log("Elem: " + elem);
            //console.log("Group: " + group);
            return group === elem;
        });
    }

    // Show ALL Groups
    function listAllGroup() {
        callAjax("/ChatApp/app/group/all", "GET", null, "application/xml", updateAllGroup);
    }

    // List all group to JOIN in ul
    function updateAllGroup(data) {
        $("ul#all-channels").html(" ");
        $(data).find("name").each(function () {
            if (! checkJoined(this.innerHTML)){
                $("ul#all-channels").append("\
                <li class='left clearfix'>\
                    <div class='chat-body clearfix'>\
                        <div class='header'>\
                            <strong class=primary-font'>" + this.innerHTML + "</strong>\
                            \
                            <div class='pull-right text-muted'>\
                                <a href='#add-" + this.innerHTML + "' class='btn btn-danger join-channel'>Join</a>\
                            </div>\
                        </div>\
                        \
                        <p>\
                            <i class='fa fa-users'></i>6\
                        </p>\
                    </div>\
                </li>");
            }
            else{
                $("ul#all-channels").append("\
                <li class='left clearfix'>\
                    <div class='chat-body clearfix'>\
                        <div class='header'>\
                            <strong class=primary-font'>" + this.innerHTML + "</strong>\
                            \
                            <div class='pull-right text-muted'>\
                                <a href='#add-" + this.innerHTML + "' class='btn btn-warning leave-channel'>Leave</a>\
                            </div>\
                        </div>\
                        \
                        <p>\
                            <i class='fa fa-users'></i>6\
                        </p>\
                    </div>\
                </li>");
            }
        });

        // Join channel Event listener
        $('.join-channel').click(function () {
            //AJAX join
            //console.log($(this).attr("href").slice(5));
            joinGroup($(this).attr("href").slice(5));
        });
    }

    // Join group
    function joinGroup(group) {
        callAjax("/ChatApp/app/group/join/", "POST", "<group><name>" + group + "</name></group>", "application/xml",
            function () {
                // Add group created as joined in ul
                //$("<li><a href='#" + group + "' class='channel'>" + group + "</a></li>").insertBefore("ul#channels li:last-child");
                //clickEvent('channel', getGroupHistory);
                listJoinedGroups();
            });
    }


    // Load Group Message
    function getGroupHistory(group) {
        currentTarget = group;
        $('.panel-heading h3').html("Channel " + group);
        $('form.send-message').attr("id", group);
        $("ul#chat").html("");
        callAjax("/ChatApp/app/history/" + group, "GET", null, "application/xml", updateHistory);
    }

    // Add messages in to chat div
    function updateHistory(data) {
        $(data).find("historyEntry").each(function (n) {
            var message = $(this).find("messsage").text();
            var email = $(this).find("email").text();
            var time = new Date($(this).find("time").text());

            $("ul#chat").append(newMessage(email, formatTime(time), message));
        });
        $("html, body").animate({scrollTop: $('#chat').height()}, 500);
    }

    // Poll new mess
    function updateNewMess(data) {
        $(data).find("historyEntry").each(function (n) {
            var message = $(this).find("messsage").text();
            var from = $(this).find("email").text();
            var time = new Date($(this).find("time").text());
            var target = $(this).find("to").text();

            if (target.startsWith("@")) { //private chat
                if (target.slice(1) == email) {
                    if (from == currentTarget){
                        $("ul#chat").append(newMessage(from, formatTime(time), message));
                    }
                }
            }
            else { // group chat
                if (target == currentTarget) {
                    $("ul#chat").append(newMessage(from, formatTime(time), message));
                }
            }

            if (!target) {
                $("ul#chat").append(newMessage(from, formatTime(time), message));
                //return;
            }

        });
        $("html, body").animate({scrollTop: $('#chat').height()}, 500);
    }

    // Load Private Message
    function getPrivateHistory(user) {
        currentTarget = "@" + user;
        $('.panel-heading h3').html("User " + user);
        $('form.send-message').attr("id", "@" + user);
        $("ul#chat").html("");
        callAjax("/ChatApp/app/history/@" + user, "GET", null, "application/xml", updateHistory);
    }

    // Format time to display
    function formatTime(time) {
        return time.getDate() + "/" + time.getMonth() + ", " + time.getHours().toString() + ":" + time.getMinutes().toString();
    }

    // Prepare li to add into chat div
    function newMessage(email, time, message) {
        return "<li class='left clearfix'>\
                <span class='chat-img pull-left'>\
                    <img src='http://placehold.it/50/029E1E/fff&text=" + email[0].toUpperCase() + "' alt='" + email + "' class='img-circle'/>\
                </span>\
                \
                <div class='chat-body clearfix'>\
                    <div class='header'>\
                        <strong class='primary-font'>" + email + "</strong>\
                        <small class='pull-right text-muted'>\
                            <span class='fa fa-clock-o'></span>" + time.toString() + "\
                        </small>\
                    </div>\
                    \
                    <p>" + message + "</p>\
                </div>\
            </li>";
    }

    // Add new channel
    function addChannel() {
        $('#add-channel').hide();
        $('#add-channel-btn').click(function () {
            $('#add-channel').show();
            $('#add-channel-btn').hide();
            $('#add-channel').on('submit', function (e) {
                e.preventDefault();
                //console.log($("#new-channel").val());
                createGroup($("#new-channel").val());
                addChannel();
                $('#add-channel-btn').show();
            });
        });
    }

    // Create new group
    function createGroup(group) {
        callAjax("/ChatApp/app/group/create/", "POST", "<group><name>" + group + "</name></group>", "application/xml",
            function () {
                joinGroup(group);
            });
    }


    // Send new messages
    $('form.send-message').on('submit', function (event) {
        event.preventDefault();
        var des = $('form.send-message').attr("id");

        var xml = composeMessage();
        console.log(xml);
        callAjax('/ChatApp/app/chat/' + des, "POST", xml, "application/xml", function () {
            $("#message").html("");
        });
    });

    // Prepare xml to POST
    function composeMessage() {
        return '<historyEntry><from><email>' + email + '</email></from><messsage>' + $("#message").val() + '</messsage><time>null</time></historyEntry>';

    }

    // Get Token
    function getToken() {
        var path = window.location.search;

        if (path.indexOf("token") !== -1) {
            var token = window.location.search.split("token=")[1];
            globalToken = token;
            localStorage.setItem("token", token);
            location.replace(window.location.origin + "/ChatApp/");
            console.log(localStorage.getItem("token"));
        } else {
            globalToken = localStorage.getItem("token");
        }
    }

    // Log out
    $('#log-out').click(function () {
        localStorage.clear();
        window.location = location.origin + '/ChatApp/';
    });
});


