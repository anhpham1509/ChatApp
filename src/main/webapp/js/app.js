/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var token="";
var email="";
$(document).ready(function () {

    
    $('#do-chat').submit(function (evt) {
        var xml=composeMessage();
        console.log(xml);
        evt.preventDefault();
        $.ajax({
            url: '/ChatApp/app/chat',
            method: "POST",
            contentType: "application/xml",
            data:xml,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (result) {
            }
        });
    });
});


function composeMessage(){
    return '<historyEntry><from><email>'+email+'</email></from><messsage>'+$("#message").val()+'</messsage><time>null</time></historyEntry>';
     //"<historyEntry><from><email>lucky7</email></from><messsage>ga ga g222asda</messsage><time>null</time></historyEntry>";

}
function onMessageSuccess(data) {
    $("#response").append('<tr><td class="received">' + $(data).find('email').text()+' said :'+$(data).find('messsage').text() + '</td></tr>');
}
function auth(url){
    var temp=$("input[name=email]").val();
    $.ajax({
            url: url,
            method: "POST",
            data:{
            'email'              : temp,
            'password'             : $("input[name=password]").val()
        },
            dataType: "text",
            success: function (result) {
                alert(result);
                email=temp;
                token=result;
                feedMessage();
            },
            failure: function (result) {
                alert(result);
            }
        });
}
function login(){
    auth("/ChatApp/app/auth/login");
}
function register(){
     auth("/ChatApp/app/auth/register");
}
function feedMessage() {
    $.ajax({
        url: '/ChatApp/app/chat',
        method: "GET",
        dataType: "xml",
        beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
        success: function (result) {
            onMessageSuccess(result);
            feedMessage();
        },
        error: function () {
            feedMessage();
        }
    });
}
