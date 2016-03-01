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

    $('#sign-in-btn').click(function (e) {
        e.preventDefault();

        $('input[type="text"], input[type="password"]').each(function () {
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
                localStorage.setItem('token', result);
                localStorage.setItem('email', email); // no need in the future
                window.location = location.origin + '/ChatApp/chat.html';
            },
            failure: function (result) {
                alert(result);
            }
        });

    });

    $('#sign-up-btn').click(function (e) {
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
            url: "/ChatApp/app/auth/register",
            method: "POST",
            data: {
                'email': email,
                'password': password
            },
            dataType: "text",
            success: function (result) {
                localStorage.setItem('token', result);
                localStorage.setItem('email', email); // no need in the future
                window.location = location.origin + '/ChatApp/chat.html';
            },
            failure: function (result) {
                alert(result);
            }
        });

    });

});
