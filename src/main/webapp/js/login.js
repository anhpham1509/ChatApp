$(document).ready(function () {

    /*
     Fullscreen background
     */
    $.backstretch("img/backgrounds/1.jpg");

    /*
     Form validation
     */
    $(".login-form input[type='text'], .login-form input[type='password']").on('focus', function () {
        $(this).removeClass('input-error');
    });

    $('.login-form').on('submit', function (e) {
        e.preventDefault();

        $(this).find('input[type="text"], input[type="password"]').each(function () {
            if ($(this).val() == "") {
                e.preventDefault();
                $(this).addClass('input-error');
            }
            else {
                $(this).removeClass('input-error');
            }
        });

        var email = $('#username').val();
        var password = $('#password').val();
        $.ajax({
            url: "/ChatApp/app/auth/login",
            method: "POST",
            data: {
                'email': email,
                'password': password
            },
            dataType: "text",
            success: function (result) {
                localStorage.setItem('userToken', result);
                localStorage.setItem('email', email);
                window.location = location.origin + '/ChatApp/chat.html';
                //listGroup();
                //listUser();
                //listUserGroup();
                //feedMessage();
            },
            failure: function (result) {
                alert(result);
            }
        });

    });


});
