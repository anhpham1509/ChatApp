<%-- 
    Document   : index
    Created on : Jan 26, 2016, 10:47:43 AM
    Author     : minhcao
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
        <script type="text/javascript" src="js/jquery-1.12.0.js"></script>
        <script type="text/javascript" src="js/app.js"></script>

    </head>
    <body>
        <div class="container chat-wrapper">
            <form id="do-chat">
                <h2 class="alert alert-success"></h2>
                <table id="response" class="table table-bordered"></table>
                <fieldset>
                    <legend>Enter your message..</legend>
                    <div class="controls">
                        <input type="text" class="input-block-level" placeholder="Your message..." id="message" style="height:60px; width:100%"/>
                        <input type="submit" class="btn btn-large btn-block btn-primary"
                               value="Send message" />
                        <button class="btn btn-large btn-block" type="button" id="leave-room">Leave
                            room</button>
                    </div>
                </fieldset>
            </form>
        <div class="container">

                <input type="text" name="email" />
                <input type="password" name="password"/>
                <input type="submit" value="Login" onclick="login()"/>
                <input type="submit" value="Register" onclick="register()"/>
        </div>
        </div>
    </body>
</html>
