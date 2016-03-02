var token = localStorage.getItem("token");
var email = localStorage.getItem('email');
var target = "";
var joinedGroups = [];

$(document).ready(function () {

    getToken();

    //Insert Username
    $('#username').html(email);

    listJoinedGroups();
    listAllUsers();
    feedMessage();

    // Hide post mess
    $('.send-message').hide();

    $('button.close').click(function () {
        $('#add-public-channel').hide();
        $('#add-public-channel-btn').show();

        $('#add-private-channel').hide();
        $('#add-private-channel-btn').show();
    });

    // Send new messages
    $('form.send-message').on('submit', function (event) {
        event.preventDefault();
        var des = $('form.send-message').attr("id");

        var xml = composeMessage();
        //console.log(xml);
        callAjax('/ChatApp/app/chat/' + des, "POST", xml, "application/xml", function () {
            $("#message").val("");
        });
    });

    // Log out
    $('#log-out').click(function () {
        localStorage.clear();
        window.location = location.origin + '/ChatApp/';
    });

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
                $("ul#users").append("<li><a href='#" + this.innerHTML + "' class='user'>" + this.innerHTML + " <span class='badge'></span></a></li>");
            }
        });

        //getPrivateHistory($('ul#users li:nth-child(2)').text());

        $('.user').click(function () {
            getPrivateHistory($(this).attr("href").slice(1));
        });
    }

// Show Groups Users joined
    function listJoinedGroups() {
        callAjax("/ChatApp/app/group/", "GET", null, "application/xml", updateGroup);
    }

// List all joined allGroups in ul
    function updateGroup(data) {
        $("ul#groups").html(" ");
        $('ul#groups').append("\
            <button class='btn btn-default' style='' id='join-modal'\
                data-toggle='modal' data-target='#join-group'>\
                Management\
            </button>");
        joinedGroups = [];
        $(data).find("name").each(function () {
            joinedGroups.push(this.innerHTML);
            $("ul#groups").append("<li><a href='#" + this.innerHTML + "' class='group'>" + this.innerHTML + " <span class='badge'></span></a></li>");
        });
        //console.log(joinedGroups);

        //getGroupHistory($('ul#channels li:nth-child(1)').text());


        $('.group').click(function () {
            getGroupHistory($(this).attr("href").slice(1));
        });

        $('#join-modal').click(function () {
            listAllGroups();
            event.preventDefault();
        });
    }

// Check joined allGroups
    function checkJoined(group) {
        return joinedGroups.some(function (elem) {
            //console.log("Elem: " + elem);
            //console.log("Group: " + group);
            return group === elem;
        });
    }

// Show ALL Groups
    function listAllGroups() {
        callAjax("/ChatApp/app/group/all", "GET", null, "application/xml", updateAllGroup);
        //addNewChannel();
    }

    // GET groups info
    function showGroups(groupName, quantity) {
        var list = "Member(s):<br>";
        callAjax("/ChatApp/app/group/" + groupName, "GET", null, "application/xml", function (result) {
            $(result).find("email").each(function () {
                console.log($(this).text());
                list += $(this).text() + "<br>";
                console.log(list);
            });

            if (!checkJoined(groupName)) {
                $("ul#all-group").append("\
                <li class='left clearfix'>\
                    <div class='chat-body clearfix'>\
                        <div class='header'>\
                            <strong class=primary-font'>" + groupName + "</strong>\
                            \
                            <div class='pull-right text-muted'>\
                                <a href='#join-" + groupName + "' class='btn btn-danger join-group'>Join</a>\
                            </div>\
                        </div>\
                        \
                        <a href='#' data-toggle='tooltip' data-placement='right' data-original-title='" + list +"'>\
                            <i class='fa fa-users'></i>" + quantity + "\
                        </a>\
                    </div>\
                </li>");
            }
            else {
                $("ul#all-group").append("\
                <li class='left clearfix'>\
                    <div class='chat-body clearfix'>\
                        <div class='header'>\
                            <strong class=primary-font'>" + groupName + "</strong>\
                            \
                            <div class='pull-right text-muted'>\
                                <a href='#leave-" + groupName + "' class='btn btn-warning leave-group'>Leave</a>\
                            </div>\
                        </div>\
                        \
                        <a href='#' data-toggle='tooltip' data-placement='right' data-original-title='" + list +"'>\
                            <i class='fa fa-users'></i>" + quantity + "\
                        </a>\
                    </div>\
                </li>");
            }

            // Join group Event listener
            $('.join-group').click(function () {
                joinGroup($(this).attr("href").slice(6));
            });

            // Leave group Event listener
            $('.leave-group').click(function () {
                leaveGroup($(this).attr("href").slice(7));
            });

            $('[data-toggle="tooltip"]').tooltip({html:true});
        });

        return list;
    }


// List all group to JOIN in ul
    function updateAllGroup(data) {
        $("ul#all-group").html(" ");

        $(data).find("group").each(function () {
            var groupName = $(this).find("name").text();
            var quantity = $(this).find("size").text();
            showGroups(groupName,quantity);

            /*if (!checkJoined(groupName)) {
                $("ul#all-group").append("\
                <li class='left clearfix'>\
                    <div class='chat-body clearfix'>\
                        <div class='header'>\
                            <strong class=primary-font'>" + groupName + "</strong>\
                            \
                            <div class='pull-right text-muted'>\
                                <a href='#join-" + groupName + "' class='btn btn-danger join-group'>Join</a>\
                            </div>\
                        </div>\
                        \
                        <a href='#' data-toggle='tooltip' data-placement='right' data-original-title='" + members +"'>\
                            <i class='fa fa-users'></i>" + quantity + "\
                        </a>\
                    </div>\
                </li>");
            }
            else {
                $("ul#all-group").append("\
                <li class='left clearfix'>\
                    <div class='chat-body clearfix'>\
                        <div class='header'>\
                            <strong class=primary-font'>" + groupName + "</strong>\
                            \
                            <div class='pull-right text-muted'>\
                                <a href='#leave-" + groupName + "' class='btn btn-warning leave-group'>Leave</a>\
                            </div>\
                        </div>\
                        \
                        <a href='#' data-toggle='tooltip' data-placement='right' data-original-title='" + members +"'>\
                            <i class='fa fa-users'></i>" + quantity + "\
                        </a>\
                    </div>\
                </li>");
            }

            // Join group Event listener
            $('.join-group').click(function () {
                joinGroup($(this).attr("href").slice(6));
            });

            // Leave group Event listener
            $('.leave-group').click(function () {
                leaveGroup($(this).attr("href").slice(7));
            });

            $('[data-toggle="tooltip"]').tooltip({html:true});
            */
        });
    }

// Join group
    function joinGroup(group) {
        callAjax("/ChatApp/app/group/join/", "POST", "<group><name>" + group + "</name></group>", "application/xml",
            function () {
                listJoinedGroups();
                listAllGroups();
            });
        event.preventDefault();
    }

// Leave group
    function leaveGroup(group) {
        callAjax("/ChatApp/app/group/leave/", "POST", "<group><name>" + group + "</name></group>", "application/xml",
            function () {
                listJoinedGroups();
                listAllGroups();
            });
        event.preventDefault();
    }

// Add messages in to chat div
    function updateHistory(data) {
        $('.send-message').show();
        $(data).find("historyEntry").each(function (n) {
            var message = $(this).find("messsage").text();
            var email = $(this).find("email").text();
            var time = new Date($(this).find("time").text());
            var filePath = $(this).find("filePath").text();
            var fileType = $(this).find("fileType").text();
            if (fileType === 'image') {
                var imgMess = "<img src='images/" + filePath + "'/>";
                $("ul#chat").append(newMessage(email, formatTime(time), imgMess));
            }
            else {
                $("ul#chat").append(newMessage(email, formatTime(time), message));
            }

        });
        $("html, body").animate({scrollTop: $('#chat').height()}, 500);
    }

// Pooling new message
    function updateNewMess(data) {
        $(data).find("historyEntry").each(function (n) {
            var message = $(this).find("messsage").text();
            var from = $(this).find("email").text();
            var time = new Date($(this).find("time").text());
            var to = $(this).find("target").text();
            var filePath = $(this).find("filePath").text();
            var fileType = $(this).find("fileType").text();
            if (fileType === 'image') {
                var imgMess = "<img src='images/" + filePath + "'/>";
                if (!to) {
                    $("ul#chat").append(newMessage(from, formatTime(time), imgMess));
                }

                //console.log(to + ": to");
                if (to.startsWith("@")) { //private chat
                    //console.log(to + ": @to");
                    //console.log(from + ": from");
                    //console.log(target + ": target");
                    if (to.slice(1) == email) {
                        if (from === target.slice(1)) {
                            $("ul#chat").append(newMessage(from, formatTime(time), imgMess));
                        }
                        else {
                            //badge into user that send mess

                            //console.log(from + "is different from: " + target.slice(1));
                            //console.log($("a.user[href='#" + from + "']").find(".badge"));
                            var newMessCount = $("a.user[href='#" + from + "'] > span.badge").html();
                            //console.log("count: " + newMessCount);
                            if (newMessCount === "") {
                                //console.log("Current: 0");
                                $("a.user[href='#" + from + "'] > span.badge").html("1");
                                //console.log("Switch to: 1");
                            }
                            else {
                                newMessCount = parseInt(newMessCount);
                                //console.log("Current: " + newMessCount);
                                newMessCount += 1;
                                $("a.user[href='#" + from + "'] > span.badge").html(newMessCount.toString());
                                //console.log("Switch to: " + newMessCount);
                            }
                        }
                    }
                }
                else { // group chat
                    //console.log(to + ": *to");
                    if (to == target) {
                        $("ul#chat").append(newMessage(from, formatTime(time), imgMess));
                    }
                    else {
                        //badge into group that send mess

                        //console.log(to + "is different from: " + target.slice(1));
                        //console.log($("a.user[href='#" + to + "']").find(".badge"));
                        newMessCount = $("a.channel[href='#" + to + "'] > span.badge").html();
                        //console.log("count: " + newMessCount);
                        if (newMessCount === "") {
                            //console.log("Current: 0");
                            $("a.channel[href='#" + to + "'] > span.badge").html("1");
                            //console.log("Switch to: 1");
                        }
                        else {
                            newMessCount = parseInt(newMessCount);
                            //console.log("Current: " + newMessCount);
                            newMessCount += 1;
                            $("a.channel[href='#" + to + "'] > span.badge").html(newMessCount.toString());
                            //console.log("Switch to: " + newMessCount);
                        }
                    }
                }

            } else {

                if (!to) {
                    $("ul#chat").append(newMessage(from, formatTime(time), message));
                    //console.log(to + ": !to");
                }
                else {
                    //console.log(to + ": to");
                    if (to.startsWith("@")) { //private chat
                        //console.log(to + ": @to");
                        //console.log(from + ": from");
                        //console.log(target + ": target");
                        if (to.slice(1) == email) {
                            if (from === target.slice(1)) {
                                $("ul#chat").append(newMessage(from, formatTime(time), message));
                            }
                            else {
                                //badge into user that send mess

                                //console.log(from + "is different from: " + target.slice(1));
                                //console.log($("a.user[href='#" + from + "']").find(".badge"));
                                var newMessCount = $("a.user[href='#" + from + "'] > span.badge").html();
                                //console.log("count: " + newMessCount);
                                if (newMessCount === "") {
                                    //console.log("Current: 0");
                                    $("a.user[href='#" + from + "'] > span.badge").html("1");
                                    //console.log("Switch to: 1");
                                }
                                else {
                                    newMessCount = parseInt(newMessCount);
                                    //console.log("Current: " + newMessCount);
                                    newMessCount += 1;
                                    $("a.user[href='#" + from + "'] > span.badge").html(newMessCount.toString());
                                    //console.log("Switch to: " + newMessCount);
                                }
                            }
                        }
                    }
                    else { // group chat
                        //console.log(to + ": *to");
                        if (to == target) {
                            $("ul#chat").append(newMessage(from, formatTime(time), message));
                        }
                        else {
                            //badge into group that send mess

                            //console.log(to + "is different from: " + target.slice(1));
                            //console.log($("a.user[href='#" + to + "']").find(".badge"));
                            newMessCount = $("a.channel[href='#" + to + "'] > span.badge").html();
                            //console.log("count: " + newMessCount);
                            if (newMessCount === "") {
                                //console.log("Current: 0");
                                $("a.channel[href='#" + to + "'] > span.badge").html("1");
                                //console.log("Switch to: 1");
                            }
                            else {
                                newMessCount = parseInt(newMessCount);
                                //console.log("Current: " + newMessCount);
                                newMessCount += 1;
                                $("a.channel[href='#" + to + "'] > span.badge").html(newMessCount.toString());
                                //console.log("Switch to: " + newMessCount);
                            }
                        }
                    }
                }
            }

        });
        $("html, body").animate({scrollTop: $('#chat').height()}, 500);
    }

// Load Private Message
    function getPrivateHistory(user) {
        target = "@" + user;
        $("a.user[href='#" + user + "'] span").html("");
        $('.panel-heading h3').html("User " + user);
        $('form.send-message').attr("id", "@" + user);
        $("ul#chat").html("");
        callAjax("/ChatApp/app/history/@" + user, "GET", null, "application/xml", updateHistory);
    }

// Load Group Message
    function getGroupHistory(group) {
        target = group;
        $("a.group[href='#" + group + "'] span").html("");
        $('.panel-heading h3').html("Group " + group);
        $('form.send-message').attr("id", group);
        $("ul#chat").html("");
        callAjax("/ChatApp/app/history/" + group, "GET", null, "application/xml", updateHistory);
    }

// Format time to display
    function formatTime(time) {
        var today = new Date();
        var date = time.getDate();
        var month = parseInt(time.getMonth()) + 1;

        if (today.getDate() !== date) {
            return formatDigits(date) + "/" + formatDigits(month);
        }
        else {
            return formatDigits(time.getHours()) + ":" + formatDigits(time.getMinutes().toString());
        }
    }

// Format 2 digits
    function formatDigits(number) {
        if (number < 9) {
            return "0" + number.toString();
        }
        else {
            return number.toString();
        }
    }

// Prepare li to add into chat div
    function newMessage(email, time, message) {
        if (email === "System") {
            return "<li class='left clearfix'>\
                <span class='chat-img pull-left'>\
                    <i class='fa fa-3x fa-laptop'></i>\
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
                    <p><i>" + message + "</i></p>\
                </div>\
            </li>";
        }
        return "<li class='left clearfix'>\
                <span class='chat-img pull-left'>\
                    <img src='/ChatApp/img/usr/" + email[0].toUpperCase() + ".png' alt='" + email + "' class='img-circle'/>\
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
    function addNewChannel() {
        $('#add-public-channel').hide();
        $('#add-public-channel-btn').show();

        $('#add-private-channel').hide();
        $('#add-private-channel-btn').show();

        $('#add-public-channel-btn').click(function () {
            $('#add-public-channel').show();

            $('#add-public-channel-btn').hide();
            $('#add-private-channel-btn').hide();

            $('#add-public-channel').on('submit', function (e) {
                e.preventDefault();
                //console.log($("#new-channel").val());
                createPublicGroup($("#public-channel").val());
                addNewChannel();
                $("#public-channel").val("");
            });
        });


        $('#add-private-channel-btn').click(function () {
            $('#add-private-channel').show();

            $('#add-private-channel-btn').hide();
            $('#add-public-channel-btn').hide();

            $('#add-private-channel').on('submit', function (e) {
                e.preventDefault();
                //console.log($("#new-channel").val());
                createPrivateGroup($("#private-channel").val());
                addNewChannel();
                $("#private-channel").val("");
            });
        });
    }

// Create new public group
    function createPublicGroup(group) {
        callAjax("/ChatApp/app/group/createPublic/", "POST", "<group><name>" + group + "</name></group>", "application/xml",
            function () {
                joinGroup(group);
            });
    }

// Create new private group
    function createPrivateGroup(group) {
        callAjax("/ChatApp/app/group/createPrivate/", "POST", "<group><name>" + group + "</name></group>", "application/xml",
            function () {
                // Select people to join this group
                //joinGroup(group);
            });
    }

// Prepare xml to POST
    function composeMessage() {
        return '<historyEntry><origin><email>' + email + '</email></origin><messsage>' + $("#message").val() + '</messsage><time>null</time></historyEntry>';

    }

});

// Get Token
function getToken() {
    var path = window.location.search;

    if (path.indexOf("token") !== -1) {
        var token = window.location.search.split("token=")[1];
        //globalToken = token;
        localStorage.setItem("token", token);
        location.replace(window.location.origin + "/ChatApp/");
        console.log(localStorage.getItem("token"));
    } else {
        token = localStorage.getItem("token");
    }
}

// Sending images
function sendImage() {
    event.preventDefault();
    if (!target) {
        return;
    }
    var imgURL = "/ChatApp/app/chat/image/" + target;
    var file = $('#send-image').get(0).files[0];
    var formData = new FormData();
    formData.append('file', file);
    $.ajax({
        type: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        url: imgURL,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {
            //console.log("success");
            //console.log(data);
        },
        error: function (data) {
            //console.log("error");
            //console.log(data);
        }
    });

}


