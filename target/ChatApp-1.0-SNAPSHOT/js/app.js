/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    feedMessage();
    $('#do-chat').submit(function (evt) {
        evt.preventDefault();
        $.ajax({
            url: '/ChatApp/app/chat',
            method: "POST",
            data: $("#message").val(),
            contentType: "text/plain",
            dataType: "text",
            success: function (result) {
            }
        });
    });
});

function onMessageSuccess(message) {
    $("#response").append('<tr><td class="received">' + message + '</td></tr>');
}

function feedMessage() {
    $.ajax({
        url: '/ChatApp/app/chat',
        method: "GET",
        dataType: "text",
        success: function (result) {
            onMessageSuccess(result);
            feedMessage();
        },
        error: function(){
            feedMessage();
        }
    });
}
