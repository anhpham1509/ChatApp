$(document).ready(function() {
    
    var globalToken = token = "";
    var email = "";

    getToken();

    if (localStorage.getItem('userToken')) {
        token = localStorage.getItem('userToken');
    }

    if (localStorage.getItem('email')) {
        email = localStorage.getItem('email');
    }

    $('')

    feedMessage();
    listGroup();
    listUser();

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
                $("html, body").animate({ scrollTop: $('#chat').height()}, 500);
            },
            error: function () {
                feedMessage();
            }
        });
    }

    //Ajax
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
    
    //Show Users
    function listUser() {
        callAjax("/ChatApp/app/user/", "GET", null, "application/xml", updateUser);
    }
    
    function updateUser(data) {
        $("ul#users").html(" ");
        $(data).find("email").each(function () {
            $("ul#users").append("<li><a class='@" + this.innerHTML +"'>" + this.innerHTML + "</a></li>");
        });
        getPrivateHistory($('ul#users li:nth-child(3)').text());
    }
    
    //Show Groups
    function listGroup() {
        callAjax("/ChatApp/app/group/", "GET", null, "application/xml", updateGroup);
    }
    
    function updateGroup(data) {
        $("ul#channels").html(" ");
        $(data).find("name").each(function () {
            $("ul #channels").append("<li class='channel'><a href='#'>" + this.innerHTML + "</a></li>");
        });
        $('ul#channels').append("<li><form id='add-channel'><div class='form-group input-group'><input class='form-control input-group-sm' placeholder='Add new channel' type='text'>" +
            "</div></form></li>");

        //getGroupHistory($('ul#channels li:nth-child(2)').text());
    }
    
    
    //Insert Username
    
    //Load Message History

        // Group Message
    function getGroupHistory(group) {
        $('.panel-heading h3').html("Channel " + group);
        $('form.send-message').attr("id", group);
        $("ul#chat").html("");
        callAjax("/ChatApp/app/history/"+ group, "GET", null, "application/xml", updateHistory);
    }

    function updateHistory(data) {
        $(data).find("historyEntry").each(function (n) {
            var message = $(this).find("messsage").text();
            var email = $(this).find("email").text();
            var time = new Date($(this).find("time").text());
            time = formatTime(time);
            $("ul#chat").append(newMessage(email, time, message));
        });
        $("html, body").animate({ scrollTop: $('#chat').height()}, 500);
    }

        // Private Message
    function getPrivateHistory(user) {
        $('.panel-heading h3').html("User " + user);
        $('form.send-message').attr("id", "@" + user);
        $("ul#chat").html("");
        callAjax("/ChatApp/app/history/@" + user, "GET", null, "application/xml", updateHistory);
    }

    // Support Functions

    function formatTime(time){
        return time.getDate() + "/" + time.getMonth() + ", " + time.getHours().toString() + ":" + time.getMinutes().toString();
    }

    function newMessage(email, time, message){
        return "<li class='left clearfix'>\
                <span class='chat-img pull-left'>\
                    <img src='http://placehold.it/50/029E1E/fff&text=" + email[0].toUpperCase() + "' alt='" + email + "' class='img-circle'/>\
                </span>\
                \
                <div class='chat-body clearfix'>\
                    <div class='header'>\
                        <strong class='primary-font'>" + email + "</strong>\
                        <small class='pull-right text-muted'>\
                            <span class='fa fa-clock-o'></span>" + time.toString() +"\
                        </small>\
                    </div>\
                    \
                    <p>" + message + "</p>\
                </div>\
            </li>";
    }

    $('ul#channels').click(function(e){
        e.preventDefault();
        var elem = $(this);
        console.log(elem);
        alert(elem.attr("class").toString());
        if (elem.attr("class").match("channel")){
            getGroupHistory(elem.val());
        }
        alert("Hello World");
    });

    //send mess

    $('form.send-message').on('submit',function(event){
        event.preventDefault();
        var des = $('form.send-message').attr("id");

        var xml = composeMessage();
        console.log(xml);
        callAjax('/ChatApp/app/chat/' + des,"POST", xml, "application/xml", function(){});
    });

    function composeMessage() {
        return '<historyEntry><from><email>' + email + '</email></from><messsage>' + $("#message").val() + '</messsage><time>null</time></historyEntry>';

    }
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