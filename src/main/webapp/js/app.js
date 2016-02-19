/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    var globalToken = token = "";
    var email = "";
    var inChatWith = "";
    var user = "";

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
    
    $("#uploadImage").submit(function(evt){
        console.log("in");
        evt.preventDefault();
        var file = $('input[name="file"]').get(0).files[0];
        var formData = new FormData(this);
        formData.append('file', file);
        $.ajax({
            type:'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            url: $(this).attr('action'),
            data:formData,
            cache:false,
            contentType: false,
            processData: false,
            success:function(data){
                console.log("success");
                console.log(data);
                setImage(data);
            },
            error: function(data){
                console.log("error");
                console.log(data);
            }
        });
    });
});
function setImage(fileName){
    $("#uploadedImage").attr("src","./images/"+fileName);
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

function normalChat() {
    var xml = composeMessage();
    console.log(xml);

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
    $("input[name=sendMessage]").val("Send Group Message").attr('onclick', '').unbind().click(groupChat);
    inChatWith = $("select[name=joinedgrouplist]").val();
    // get history for group chat
    doAction("/ChatApp/app/history/" + $("select[name=joinedgrouplist]").val(), "GET", null, "application/xml", updateHistory);
}
function toSingleChat() {
    $("#response").html(" ");
    $("input[name=sendMessage]").val("Send Private Message").attr('onclick', '').unbind().click(singleChat);
    inChatWith = $("select[name=userlist]").val();;
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
function updateHistory(data) {
    console.log(data);
    $(data).find("historyEntry").each(function (n) {
        var message = $(this).find("messsage").text();
        var email = $(this).find("from").text();
        var time = new Date($(this).find("time").text());
        $("#response").append('<tr><td class="received">' + time.toString() + " : " + email + " said : " + message + '</td></tr>');
    });
}
function handleNewMessage(data) {
    console.log(data);
    $(data).find("historyEntry").each(function (n) {
        var message = $(this).find("messsage").text();
        var email = $(this).find("from").text();
        var time = new Date($(this).find("time").text());
        var target = $(this).find("to").text();
        onMessageSuccess(email, message, time, target);
    });
}
function onMessageSuccess(email, message, time, target) {
    // for sender display
    if(!target){
        $("#response").append('<tr><td class="received">' + time.toString() + " : " + email + " said : " + message + '</td></tr>');
        return;
    }
    console.log("in chat with: "+ inChatWith);
    
    // in group chat
    if(!target.startsWith("@")){
        if(target !== inChatWith){
            return;
        }
    }
    
    // in private chat
    if(target.startsWith("@")){
        if(email !== inChatWith){
            return;
        }
    }
    $("#response").append('<tr><td class="received">' + time.toString() + " : " + email + " said : " + message + '</td></tr>');
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
            user = $("input[name=email]").val();
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
            handleNewMessage(result);
            feedMessage();
        },
        error: function () {
            feedMessage();
        }
    });
}