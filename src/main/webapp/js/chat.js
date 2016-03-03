var token = localStorage.getItem("token");
var email = localStorage.getItem('email');
var target = "";
var joinedGroups = [];
var isAdmin;



$(document).ready(function () {

    //getToken();
    getUserInfo();
    listJoinedGroups();
    listAllUsers();
    feedMessage();
    //unreadMess();

    //Insert Username
    $('#username').html(email);

    // Hide post mess
    $('.send-message').hide();
    getAlerts();

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

    $("#openAlert").click(function () {
        console.log("in alert", isAdmin);
        if (!isAdmin) {
            alert("Only admins could send alerts !");
        }
    });

    $("#sendAlert").click(function () {
        console.log($("#alertList").val());
        var list = "";
        var message = $("#alertMessage").val();
        $("#alertList").val().forEach(function (user) {
            console.log(user);
            list = list + user + ",";
        });

        if (!list || !message) {
            alert("Please fill in required inputs!");
            return;
        }
        // remove last comma
        list = list.substring(0, list.length - 1);
        var xml = "<alert><time>null</time><origin>" + "<email>" + email + "</email>" + "</origin>" + "<targetList>" + list + "</targetList><message>" + message + "</message></alert>";

        // send request
        $.ajax({
            url: '/ChatApp/app/alert',
            method: "POST",
            contentType: "application/xml",
            data: xml,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (result) {
                alert("Successfully sent alerts!");
                $('#alert-modal').modal('toggle');
            },
            error: function (err) {
                console.log(err);
            }
        });
    });

    $("#promoteUser").click(function () {
        if (!isAdmin) {
            alert("Only admins are able to promote!");
            return;
        }
        callAjax("/ChatApp/app/user/promote/", "POST", "<user>" + "<email>" + $("select[id=promoteList]").val() + "</email>" + "</user>", "application/xml", function () {
            alert("Successfully promoted!");
            $('#user-mgmt-modal').modal('toggle');
        });
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

// Get user's info
    function getUserInfo() {
        callAjax("/ChatApp/app/user/role", "GET", null, "application/xml", function (data) {
            if (data !== "Admin") {
                isAdmin = false;
            } else {
                isAdmin = true;
            }
        });
    }

// List all users in ul
    function updateUser(data) {
        $("ul#users").html(" ");
        $('ul#users').append("\
            <button class='btn btn-default' style='' id='user-mgmt'\
                data-toggle='modal' data-target='#user-mgmt-modal'>\
                Management\
            </button>");
        $(data).find("email").each(function () {
            if (this.innerHTML !== email) {
                $("select[name=userlist]").append("<option value='" + this.innerHTML + "'>" + this.innerHTML + "</option>");
                $("ul#users").append("<li><a href='#" + this.innerHTML + "' class='user'>" + this.innerHTML + " <span class='badge'></span></a></li>");
            }
        });
        $("select[name=userlist]").multiselect({maxHeight: 200});

        //getPrivateHistory($('ul#users li:nth-child(2)').text());

        $('.user').click(function () {
            getPrivateHistory($(this).attr("href").slice(1));
        });

        unreadMess();
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
        });

        unreadMess();
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
    }

    // GET groups info
    function showGroups(groupName, quantity) {
        var list = "Members:<br>";
        callAjax("/ChatApp/app/group/" + groupName, "GET", null, "application/xml", function (result) {
            $(result).find("email").each(function () {
                list += $(this).text() + "<br>";
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
                        <a href='#' data-toggle='tooltip' data-placement='right' data-original-title='" + list + "'>\
                            <i class='fa fa-users'></i>" + quantity + "\
                        </a>\
                    </div>\
                </li>");
            } else {
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
                        <a href='#' data-toggle='tooltip' data-placement='right' data-original-title='" + list + "'>\
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

            // Show group members
            $('[data-toggle="tooltip"]').tooltip({html: true});
        });
    }


// List all group to JOIN in ul
    function updateAllGroup(data) {
        $('#modal-title').html("Group management");
        $("ul#all-group").html(" ");
        $('#modal-footer').html("<button class='btn btn-success' id='add-group-btn'>Add new group</button>");

        $(data).find("group").each(function () {
            var groupName = $(this).find("name").text();
            var quantity = $(this).find("size").text();
            showGroups(groupName, quantity);
        });

        // Add new group
        addGroup();
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
            } else {
                $("ul#chat").append(newMessage(email, formatTime(time), message));
            }

        });
        $("html, body").animate({scrollTop: $('#chat').height()}, 500);
    }

// Pooling new message
    function updateNewMess(data) {
        if ($(data).find("alert").length > 0) {
            displayAlert(data);
            return;
        }

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
                        } else {
                            //badge into user that send mess

                            //console.log(from + "is different from: " + target.slice(1));
                            //console.log($("a.user[href='#" + from + "']").find(".badge"));
                            var newMessCount = $("a.user[href='#" + from + "'] > span.badge").html();
                            //console.log("count: " + newMessCount);
                            if (newMessCount === "") {
                                //console.log("Current: 0");
                                $("a.user[href='#" + from + "'] > span.badge").html("1");
                                //console.log("Switch to: 1");
                            } else {
                                newMessCount = parseInt(newMessCount);
                                //console.log("Current: " + newMessCount);
                                newMessCount += 1;
                                $("a.user[href='#" + from + "'] > span.badge").html(newMessCount.toString());
                                //console.log("Switch to: " + newMessCount);
                            }
                        }
                    }
                } else { // group chat
                    //console.log(to + ": *to");
                    if (to == target) {
                        $("ul#chat").append(newMessage(from, formatTime(time), imgMess));
                    } else {
                        //badge into group that send mess

                        //console.log(to + "is different from: " + target.slice(1));
                        //console.log($("a.user[href='#" + to + "']").find(".badge"));
                        newMessCount = $("a.group[href='#" + to + "'] > span.badge").html();
                        //console.log("count: " + newMessCount);
                        if (newMessCount === "") {
                            //console.log("Current: 0");
                            $("a.group[href='#" + to + "'] > span.badge").html("1");
                            //console.log("Switch to: 1");
                        } else {
                            newMessCount = parseInt(newMessCount);
                            //console.log("Current: " + newMessCount);
                            newMessCount += 1;
                            $("a.group[href='#" + to + "'] > span.badge").html(newMessCount.toString());
                            //console.log("Switch to: " + newMessCount);
                        }
                    }
                }

            } else {

                if (!to) {
                    $("ul#chat").append(newMessage(from, formatTime(time), message));
                    //console.log(to + ": !to");
                } else {
                    //console.log(to + ": to");
                    if (to.startsWith("@")) { //private chat
                        //console.log(to + ": @to");
                        //console.log(from + ": from");
                        //console.log(target + ": target");
                        if (to.slice(1) == email) {
                            if (from === target.slice(1)) {
                                $("ul#chat").append(newMessage(from, formatTime(time), message));
                            } else {
                                //badge into user that send mess

                                //console.log(from + "is different from: " + target.slice(1));
                                //console.log($("a.user[href='#" + from + "']").find(".badge"));
                                var newMessCount = $("a.user[href='#" + from + "'] > span.badge").html();
                                //console.log("count: " + newMessCount);
                                if (newMessCount === "") {
                                    //console.log("Current: 0");
                                    $("a.user[href='#" + from + "'] > span.badge").html("1");
                                    //console.log("Switch to: 1");
                                } else {
                                    newMessCount = parseInt(newMessCount);
                                    //console.log("Current: " + newMessCount);
                                    newMessCount += 1;
                                    $("a.user[href='#" + from + "'] > span.badge").html(newMessCount.toString());
                                    //console.log("Switch to: " + newMessCount);
                                }
                            }
                        }
                    } else { // group chat
                        //console.log(to + ": *to");
                        if (to == target) {
                            $("ul#chat").append(newMessage(from, formatTime(time), message));
                        } else {
                            //badge into group that send mess

                            //console.log(to + "is different from: " + target.slice(1));
                            //console.log($("a.user[href='#" + to + "']").find(".badge"));
                            newMessCount = $("a.group[href='#" + to + "'] > span.badge").html();
                            //console.log("count: " + newMessCount);
                            if (newMessCount === "") {
                                //console.log("Current: 0");
                                $("a.group[href='#" + to + "'] > span.badge").html("1");
                                //console.log("Switch to: 1");
                            } else {
                                newMessCount = parseInt(newMessCount);
                                //console.log("Current: " + newMessCount);
                                newMessCount += 1;
                                $("a.group[href='#" + to + "'] > span.badge").html(newMessCount.toString());
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
        } else {
            return formatDigits(time.getHours()) + ":" + formatDigits(time.getMinutes().toString());
        }
    }

// Format 2 digits
    function formatDigits(number) {
        if (number < 9) {
            return "0" + number.toString();
        } else {
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

// Add new group
    function addGroup() {
        $('#add-group-btn').click(function () {
            $('#modal-title').html("Create a new group");
            $('ul#all-group').html("\
            <form id='add-group-form'>\
                \
                <div class='form-group'>\
                    <label for='checkbox'>Group type</label>\
                    <div class='onoffswitch'>\
                        <input type='checkbox' name='onoffswitch' class='onoffswitch-checkbox' id='myonoffswitch' checked>\
                        <label class='onoffswitch-label' for='myonoffswitch'>\
                            <span class='onoffswitch-inner'></span>\
                            <span class='onoffswitch-switch'></span>\
                        </label>\
                    </div>\
                </div>\
                \
                <div class='form-group'>\
                    <label for='group-name'>Group name</label>\
                    <input id='group-name' class='form-control' type='text' placeholder='Enter group name here'>\
                </div>\
                \
                <div class='form-group'>\
                    <label for='invite-people'>Invite others to join (optional)</label>\
                    <input id='invite-people' class='form-control' type='text' placeholder='Enter username here to invite'>\
                </div>\
            </form>\
            ");
            $('#modal-footer').html("<button id='create-group' type='submit' class='btn btn-danger'>Create group</button>");

            var isPrivate = false;

            $('#myonoffswitch').click(function () {
                isPrivate = !isPrivate;
            });

            $('#create-group').click(function () {
                var newGroup = $('#group-name').val();
                if (isPrivate) {
                    createPrivateGroup(newGroup);
                } else {
                    createPublicGroup(newGroup);
                }
                listJoinedGroups();
                listAllGroups();
                event.preventDefault();
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

    // Unread mess
    function unreadMess() {
        callAjax("/ChatApp/app/history/unread", "GET", null, "application/xml", function(data){
            var unreads = data.split("|");
            for (var idx in unreads){
                if (unreads[idx] !== "") {
                    //console.log(unreads[idx]);
                    var info = unreads[idx].split(":");
                    //console.log(info);
                    if (info[0].startsWith("@")) {
                        $("a.user[href='#" + info[0].substr(1) + "'] > span.badge").html(info[1]);
                    }
                    else {
                        $("a.group[href='#" + info[0] + "'] > span.badge").html(info[1]);
                    }
                }
            }
        });
    }


// Get alerts when logging in
    function getAlerts() {
        callAjax("/ChatApp/app/alert", "GET", null, "application/xml", displayAlert);
    }

// Dislay alert
    function displayAlert(alerts) {
        console.log("display", alerts);
        if (!alerts) {
            return;
        }
        $(alerts).find("alert").each(function (index) {

            var id = $(this).find("id").text();
            var message = $(this).find("message").text();
            var from = $(this).find("origin").text();
            var time = new Date($(this).find("time").text());
            $("#dialogs").append("<div id='dialog-" + index + "' title='Alert'><span class='ui-state-default'><span class='ui-icon ui-icon-info' style='float:left; margin:0 7px 0 0;'></span></span><p id='dialog-message-" + index + "'></p></div>");
            $("#dialog-message-" + index).html("At: " + time + "<br>" + "From: " + from + "<br>Message: " + message);
            $("#dialog-" + index).dialog({
                modal: true,
                draggable: false,
                resizable: false,
                show: 'blind',
                hide: 'blind',
                width: 400,
                buttons: [
                    {
                        text: "Confirm that i have read",
                        icons: {
                            primary: "ui-icon-check"
                        },
                        click: function () {
                            var $this = $(this);
                            callAjax("/ChatApp/app/alert/" + id, "POST", null, null, function () {
                                $this.dialog("close");
                            });
                        }
                    }
                ]
            });
        });
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
