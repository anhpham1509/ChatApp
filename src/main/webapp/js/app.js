/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var isLoggedIn = false;
var globalToken = token = "";
var email = "";
var inChatWith = "";
var user = "";
$(document).ready(function () {
    $("#dialog").hide();
    getToken();
    if (localStorage.getItem('userToken')) {
        token = localStorage.getItem('userToken');
    }

    $("#authTest").click(function (evt) {
        console.log("1 :" + window.globalToken);
        $.ajax({
            url: "/ChatApp/app/auth/test",
            method: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", window.globalToken);
            },
            success: function (result) {
                alert(result);
            },
            failure: function (result) {
                alert(result);
            }
        });
    });
});
function sendAlert() {
    console.log($("select[name=userlist]").val());
    var xml = "<alert><time>null</time><origin>" + "<email>" + user + "</email>" + "</origin>" + "<targetList>" + $("select[name=userlist]").val() + "</targetList><message>" + $("#alertMessage").val() + "</message></alert>";
    console.log(xml);
    $.ajax({
        url: '/ChatApp/app/alert',
        method: "POST",
        contentType: "application/xml",
        data: xml,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        success: function (result) {
            console.log(result);
        },
        error: function (err) {
            console.log(err);
        }
    });
}
function sendImage() {
    var sendingUrl;
    console.log("in" + inChatWith);
    event.preventDefault();
    if (!inChatWith) {
        return;
    }
    if (inChatWith.startsWith("@")) {
        sendingUrl = "/ChatApp/app/chat/image/@" + $("select[name=userlist]").val();
    } else {
        sendingUrl = "/ChatApp/app/chat/image/" + $("select[name=joinedgrouplist]").val();
    }
    var file = $('input[name="file"]').get(0).files[0];
    var formData = new FormData();
    formData.append('file', file);
    $.ajax({
        type: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        url: sendingUrl,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {
            console.log("success");
            console.log(data);
        },
        error: function (data) {
            console.log("error");
            console.log(data);
        }
    });

}

function setImage(fileName) {
    $("#uploadedImage").attr("src", "./images/" + fileName);
}

function getToken() {
    var path = window.location.search;
    console.log(path);
    if (path.indexOf("token") !== -1) {
        var token = window.location.search.split("token=")[1];
        globalToken = token;
        localStorage.setItem("token", token);
        location.replace(window.location.origin + "/ChatApp/");
        console.log(localStorage.getItem("token"));
    } else {
        globalToken = localStorage.getItem("token");
        console.log("2 :" + globalToken);
    }
}

//function normalChat() {
//    var xml = composeMessage();
//    console.log(xml);
//
//    $.ajax({
//        url: '/ChatApp/app/chat',
//        method: "POST",
//        contentType: "application/xml",
//        data: xml,
//        beforeSend: function (xhr) {
//            xhr.setRequestHeader("Authorization", token);
//        },
//        success: function (result) {
//        }
//    });
//}

function toGroupChat() {
    $("#response").html(" ");
    $("input[name=sendMessage]").val("Send Group Message").attr('onclick', '').unbind().click(groupChat);
    inChatWith = $("select[name=joinedgrouplist]").val();
    console.log(inChatWith);
    // get history for group chat
    doAction("/ChatApp/app/history/" + $("select[name=joinedgrouplist]").val(), "GET", null, "application/xml", updateHistory);
}
function toSingleChat() {
    $("#response").html(" ");
    $("input[name=sendMessage]").val("Send Private Message").attr('onclick', '').unbind().click(singleChat);
    inChatWith = "@" + $("select[name=userlist]").val();
    console.log(inChatWith);
    // get history for private chat
    doAction("/ChatApp/app/history/@" + $("select[name=userlist]").val(), "GET", null, "application/xml", updateHistory);
}
function singleChat() {
    var xml = composeMessage();
    console.log(xml);
    //    evt.preventDefault();
    $.ajax({
        url: '/ChatApp/app/chat/@' + $("select[name=userlist]").val(),
        method: "POST",
        contentType: "application/xml",
        data: xml,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        success: function (result) {
        }
    });
}
function groupChat() {
    var xml = composeMessage();
    console.log(xml);
    //    evt.preventDefault();
    $.ajax({
        url: '/ChatApp/app/chat/' + $("select[name=joinedgrouplist]").val(),
        method: "POST",
        contentType: "application/xml",
        data: xml,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        success: function (result) {
        }
    });
}
function promoteUser() {
    doAction("/ChatApp/app/user/promote/", "POST", "<user>" + "<email>" + $("select[name=userlist]").val() + "</email>" + "</user>", "application/xml", noftifai);
}
function createPrivateGroup() {
    doAction("/ChatApp/app/group/createPrivate/", "POST", "<group><name>" + $("input[name=groupName]").val() + "</name></group>", "application/xml", listGroup);
}
//Temporarily one user can add more user to users tag in real front end
function addUserPrivate() {
    doAction("/ChatApp/app/group/addUser/" + $("select[name=grouplist]").val(), "POST", "<users><user>" + "<email>" + $("select[name=userlist]").val() + "</email>" + "</user></users>", "application/xml", noftifai);
}
function createGroup() {
    doAction("/ChatApp/app/group/createPublic/", "POST", "<group><name>" + $("input[name=groupName]").val() + "</name></group>", "application/xml", listGroup);
}
function listGroup() {
    doAction("/ChatApp/app/group/all", "GET", null, "application/xml", updateGroup);
}
function listUser() {
    doAction("/ChatApp/app/user/", "GET", null, "application/xml", updateUser);
}
function listUserGroup() {
    doAction("/ChatApp/app/group/", "GET", null, "application/xml", updateUserGroup);
}
function noftifai(data) {
    alert(data);
}
function getUnread(){
    doAction("/ChatApp/app/history/unread/", "GET", null, "application/xml",doSomethingElse);
}
function updateUser(data) {
    $("select[name=userlist]").html(" ");
    $(data).find("user").each(function () {

        //var value = $(this).find('name').attr('value');
        $("select[name=userlist]").append("<option>" + this.innerHTML + "</option>");
    });

}
function updateUserGroup(data) {
    $("select[name=joinedgrouplist]").html(" ");
    $(data).find("group").each(function () {

        //var value = $(this).find('name').attr('value');
        $("select[name=joinedgrouplist]").append("<option>" + $(this).find("name").text() + "</option>");
    });

}
function updateGroup(data) {
    $("select[name=grouplist]").html(" ");
    $(data).find("group").each(function () {
        $("select[name=grouplist]").append("<option>" + $(this).find("name").text() + "</option>");
    });

}
function joinGroup() {
    doAction("/ChatApp/app/group/join/", "POST", "<group><name>" + $("select[name=grouplist]").val() + "</name></group>", "application/xml", doSomething);
}
function leaveGroup() {
    doAction("/ChatApp/app/group/leave/", "POST", "<group><name>" + $("select[name=grouplist]").val() + "</name></group>", "application/xml", doSomething);
}
function doSomething(data) {
    $("#response").html(" ");
    doAction("/ChatApp/app/group/"+$("select[name=grouplist]").val(), "GET", null, "application/xml", doSomethingElse);
    alert(data);
}
function doSomethingElse(data){
    console.log(data);
}
function updateHistory(data) {
    console.log(data);
    $(data).find("historyEntry").each(function (n) {
        var message = $(this).find("messsage").text();
        var email = $(this).find("origin").text();
        var time = new Date($(this).find("time").text());
        var filePath = $(this).find("filePath").text();
        var fileType = $(this).find("fileType").text();
        if (!fileType) {
            $("#response").append('<tr><td class="received">' + time.toString() + " : " + email + " said : " + message + '</td></tr>');
        } else {
            switch (fileType) {
                case "image":
                    $("#response").append('<tr><td class="received">' + time.toString() + " : " + email + " uploaded : " + "<img src='images/" + filePath + "'/></td></tr>");
                    break;
                default:

            }
        }
    });
}
function handleNewMessage(data) {
    console.log("newdata", data);
    if ($(data).find("alert").length > 0) {
        displayAlert(data);
    }

    $(data).find("historyEntry").each(function (n) {
        var message = $(this).find("messsage").text();
        var from = $(this).find("origin").text();
        var time = new Date($(this).find("time").text());
        var target = $(this).find("target").text();
        var filePath = $(this).find("filePath").text();
        var fileType = $(this).find("fileType").text();
        if (fileType === 'image') {
            onImageSuccess(from, message, time, target, filePath, fileType);
        } else {
            onMessageSuccess(from, message, time, target);
        }
    });
}

function onImageSuccess(from, message, time, target, filePath, fileType) {
    console.log(arguments);
    if (fileType !== "image") {
        return;
    }

    if (!target) {
        $("#response").append('<tr><td class="received">' + time.toString() + " : " + from + " uploaded : " + "<img src='images/" + filePath + "'/></td></tr>");
    }

    // in group chat
    if (!target.startsWith("@")) {
        if (target !== inChatWith) {
            return;
        }
    }

    // in private chat
    if (target.startsWith("@")) {
        if (from !== inChatWith.slice(1)) {
            return;
        }
    }

    $("#response").append('<tr><td class="received">' + time.toString() + " : " + from + " uploaded : " + "<img src='images/" + filePath + "'/></td></tr>");
}

function onMessageSuccess(from, message, time, target) {
    console.log("in chat with: " + inChatWith + from + target);
    if (!target) {
        $("#response").append('<tr><td class="received">' + time.toString() + " : " + from + " said : " + message + '</td></tr>');
    }

    // in group chat
    if (!target.startsWith("@")) {
        if (target !== inChatWith) {
            return;
        }
    }

    // in private chat
    if (target.startsWith("@")) {
        if (from !== inChatWith.slice(1)) {
            return;
        }
    }
    $("#response").append('<tr><td class="received">' + time.toString() + " : " + from + " said : " + message + '</td></tr>');
}
function doAction(url, method, data, contentType, callback) {
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
function composeMessage() {
    return '<historyEntry><origin><email>' + email + '</email></origin><messsage>' + $("#message").val() + '</messsage><time>null</time></historyEntry>';
    //"<historyEntry><from><email>lucky7</email></from><messsage>ga ga g222asda</messsage><time>null</time></historyEntry>";

}

function auth(url) {
    var temp = $("input[name=email]").val();
    $.ajax({
        url: url,
        method: "POST",
        data: {
            'email': temp,
            'password': $("input[name=password]").val()
        },
        dataType: "text",
        success: function (result) {
            user = $("input[name=email]").val();
            alert(result);
            email = temp;
            token = result;
            localStorage.setItem('userToken', token);
            getAlerts();
            listGroup();
            listUser();
            listUserGroup();
            if (!isLoggedIn)
                feedMessage();
            isLoggedIn = true;
        },
        failure: function (result) {
            alert(result);
        }
    });
}
function getAlerts() {
    doAction("/ChatApp/app/alert", "GET", null, "application/xml", displayAlert);
}
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
        $("#dialogs").append("<div id='dialog-"+index+"' title='Alert'><span class='ui-state-default'><span class='ui-icon ui-icon-info' style='float:left; margin:0 7px 0 0;'></span></span><p id='dialog-message-"+index+"'></p></div>");
        $("#dialog-message-"+index).html("At: " + time + "<br>" + "From: " + from + "<br>Message: " + message);
        $("#dialog-"+index).dialog({
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
                        doAction("/ChatApp/app/alert/" + id, "POST", null, null, function () {
                            $this.dialog("close");
                        });
                    }
                }
            ]
        });
    });
}
function login() {
    auth("/ChatApp/app/auth/login");
}
function logout() { //Tam null callback
    doAction("/ChatApp/app/auth/logout/", "GET", null, null, null);
}
function register() {
    auth("/ChatApp/app/auth/register");
}
function feedMessage() {
    $.ajax({
        url: "/ChatApp/app/chat",
        method: "GET",
        dataType: "xml",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        success: function (result) {
            handleNewMessage(result);
            feedMessage();
        },
        error: function () {
            feedMessage();
        }
    });
}