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
        <script src="//cdn.auth0.com/js/lock-8.2.min.js"></script>

        <!-- Setting the right viewport -->
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    </head>
    <body>
        <div class="container chat-wrapper">

            <h2 class="alert alert-success"></h2>
            <table id="response" class="table table-bordered"></table>
            <fieldset>
                <legend>Enter your message..</legend>
                <div class="controls">
                    <input type="text" class="input-block-level" placeholder="Your message..." id="message" style="height:60px; width:100%"/>
                    <input type="submit" class="btn btn-large btn-block btn-primary" name="sendMessage"
                           value="Send message" onclick="normalChat()"/>
                    <div class="container">
                        <form id="uploadImage" action="app/chat/image" method="post" enctype="multipart/form-data" onsubmit="sendImage()">
                            Select an image:
                            <input type="file" name="file" size="50" style="display:inline-block"/>
                            <input type="submit" value="Upload it"/>
                        </form>
                    </div>
                    <button class="btn btn-large btn-block" type="button" id="leave-room">Leave
                        room</button>
                </div>
            </fieldset>
            <div class="container">
                <p id="newPrivateMessage"></p>
            </div>
            <div class="container">

                <input type="text" name="email" />
                <input type="password" name="password"/>
                <input type="submit" value="Login" onclick="login()"/>
                <input type="submit" value="Register" onclick="register()"/>
            </div>
            <div class="container">
                <br>
                Group
                <input type="text" name="groupName" />
                <input type="submit" value="createGroup" onclick="createGroup()"/>
                <input type="submit" value="Create Private Group" onclick="createPrivateGroup()"/>
            </div>
            <div class="container">
                <br>
                User List
                <select name="userlist" >

                </select>
                <input type="submit" value="Send Private Message" onclick="toSingleChat()"/>
                <input type="submit" value="Promote User" onclick="promoteUser()"/>
                
            </div>
            <div class="container">
                <br>
                Group
                <select name="grouplist" >

                </select>
                <input type="submit" value="Join" onclick="joinGroup()"/>
                <input type="submit" value="Add User to Private Group User chọn tạm ở trên" onclick="addUserPrivate()"/>

            </div>
            <div class="container">
                <br>
                Joined Group 
                <select name="joinedgrouplist" >

                </select>
                <input type="submit" value="Send Group Message" onclick="toGroupChat()"/>
                <input type="submit" value="Leave" onclick="leaveGroup()"/>
            </div>   
        </div>
        <div class="container">
            <a href="app/auth">Login</a>
        </div>
        <div class="container">
            <button id="authTest">Secured REST resource</button>
        </div>
    </body>
</html>
