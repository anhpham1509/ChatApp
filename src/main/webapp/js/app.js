/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    var globalToken = token = "";
    var email = "";

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
                xhr.setRequestHeader("Authorization", + window.globalToken);
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

function normalChat() {
    var xml = composeMessage();
    console.log(xml);

    $("#response").append("<tr><td class='received'>" + message + "</td></tr>");
    $.ajax({
        url: '/ChatApp/app/chat',
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

function toGroupChat() {
    $("#response").html(" ");
    $("input[name=sendMessage]").val("Send Group Message").attr('onclick', '').click(groupChat);
    getGroupHistory();
}
function toSingleChat() {
    $("#response").html(" ");
    $("input[name=sendMessage]").val("Send Private Message").attr('onclick', '').click(singleChat);
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
        url: '/ChatApp/app/chat/*' + $("select[name=grouplist]").val(),
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
function createGroup() {
    doAction("/ChatApp/app/group/create/", "POST", "<group><name>" + $("input[name=groupName]").val() + "</name></group>", "application/xml", listGroup);
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
        $("select[name=joinedgrouplist]").append("<option>" + this.innerHTML + "</option>");
    });

}
function updateGroup(data) {
    $("select[name=grouplist]").html(" ");
    $(data).find("group").each(function () {

        //var value = $(this).find('name').attr('value');
        $("select[name=grouplist]").append("<option>" + this.innerHTML + "</option>");
    });

}
function joinGroup() {
    doAction("/ChatApp/app/group/join/", "POST", "<group><name>" + $("select[name=grouplist]").val() + "</name></group>", "application/xml", doSomething);
}
function doSomething(data) {
    $("#response").html(" ");

    alert(data);
}
function getGroupHistory() {
    doAction("/ChatApp/app/history/*" + $("select[name=grouplist]").val(), "GET", null, "application/xml", updateHistory);
}
function updateHistory(data) {
    $(data).find("historyEntry").each(function (n) {
        var message = $(this).find("messsage").text();
        var email = $(this).find("email").text();
        var time = new Date($(this).find("time").text());
        onMessageSuccess(email, message, time);
    });
}
function onMessageSuccess(email, message, time) {
    $("#response").append('<tr><td class="received">' + time.toString() + " : " + email + " said : " + message + '</td></tr>');
}
function getPrivateHistory() {

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
    return '<historyEntry><from><email>' + email + '</email></from><messsage>' + $("#message").val() + '</messsage><time>null</time></historyEntry>';
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
            alert(result);
            email = temp;
            token = result;
            localStorage.setItem('userToken', token);
            listGroup();
            listUser();
            listUserGroup();
            feedMessage();
        },
        failure: function (result) {
            alert(result);
        }
    });
}
function login() {
    auth("/ChatApp/app/auth/login");
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
            updateHistory(result);
            feedMessage();
        },
        error: function () {
            feedMessage();
        }
    });
}