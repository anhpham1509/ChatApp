/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    var globalToken = "";
    getToken();
    feedMessage();
    $("#do-chat").submit(function (evt) {
        evt.preventDefault();
        $.ajax({
            url: "/ChatApp/app/chat",
            method: "POST",
            data: $("#message").val(),
            contentType: "text/plain",
            dataType: "text",
            success: function (result) {
            }
        });
    });

    $("#authTest").click(function (evt) {
        console.log("1 :"+window.globalToken);
        $.ajax({
            url: "/ChatApp/app/auth/test",
            method: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Basic " + window.globalToken);
            },
            success: function(result){
                alert(result);
            },
            failure: function(result){
                alert(result);
            }
        });
    });
});

function getToken(){
    var path = window.location.search;
    console.log(path);
    if(path.indexOf("token")!==-1){
        var token = window.location.search.split("token=")[1];
        globalToken = token;
        localStorage.setItem("token", token);
        location.replace(window.location.origin+"/ChatApp/");
        console.log(localStorage.getItem("token"));
    } else {
        globalToken = localStorage.getItem("token");
        console.log("2 :"+globalToken);
    }
}

function onMessageSuccess(message) {
    $("#response").append("<tr><td class='received'>" + message + "</td></tr>");
}

function feedMessage() {
    $.ajax({
        url: "/ChatApp/app/chat",
        method: "GET",
        dataType: "text",
        success: function (result) {
            onMessageSuccess(result);
            feedMessage();
        },
        error: function () {
            feedMessage();
        }
    });
}
