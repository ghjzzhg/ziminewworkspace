<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

 <#-- TODO: find the way to import CSS from css component folder
  For now we are getting CSS from the opentaps_css directory inside opentaps-common.
 -->

<link rel="shortcut icon" href="/images/ofbiz.ico" />

    <script type="text/javascript">
        function writeAppDetails(appId, appName, appDescr){
            var id = document.getElementById('appId');
            var name = document.getElementById('appName');
            var description = document.getElementById('appDescr');

            id.innerHTML = appId;
            name.innerHTML = appName;
            description.innerHTML = appDescr;
        }

        function forgotPasswd(){
            //if the errorDiv is present, increase the heigth of the container
            if(document.getElementById('errorDiv')){
               document.getElementById('container').style.height='540px';
               document.getElementById('form').style.height='380px';
            }

            var forgotPasswdForm = document.getElementById('forgotpasswd');
            forgotPasswdForm.style.display='block';
        }
    </script>


<title>RexTec Home</title>
    <style type="text/css">
        @charset "UTF-8";
        /* CSS Document */

        body{
            font-family:Arial, Helvetica, sans-serif;
        }
        #top {
            height:30px;
            background:#333333;
        }
        #container{
            height:720px;
            width:100%;
            background-color:#FFFFFF;
            margin:0 0 0 0;}

        #header{
            height:122px;}

        #wrapper{
            float:left;
            width:1000px;}

        #form{
            float:left;
            width:260px;
            height:560px;
            border-right:2px dotted #333333;
            margin:30px 0 0 32px;}

        #button{
            margin-bottom:25px;
            float: left;
            width: 25%;
            width: 160px;
        }

        #button a{
            /*prevents classic html colors when clicking on the link*/
            color:#FFFFFF;
        }

        #appDescr{
            margin: 0 auto;
            width:600px;
            color:#FFFFFF;
            font-size:0.9em;
            padding: 10px;
        }
        #footer {
            height:90px;
            background:#333333;
            position: absolute;
            bottom: 0;
            width: 100%;
            padding-top: 10px;
            text-align: center;
        }
        .serviceError{
            color:#333333;
            display:block;
            font-size:0.7em;
            margin-left:8px;
            text-align:left;
            width:238px;
            padding:6px 0 6px 6px;
        }

        @charset "UTF-8";
        /* CSS Document */

        #logo{
            width:241px;
            height:49px;
            margin:15px 0 15px 32px;
            float:left;}

        #title{
            height:43px;
            width:100%;
            background-color:#CCCCCC;
            float:left;}

        #title h1{
            font-size:1.4em;
            font-family:Arial, Helvetica, sans-serif;
            color:#333333;
            margin:10px 0 0 318px;}



        #form h2{
            background-color:#FF6633;
            font-size:1em;
            color:#FFFFFF;
            width:236px;
            padding:6px 0 6px 6px;
        }

        #form h3{
            margin-top:15px;}


        #form h3 a{
            color:#FF6633;
            text-decoration:underline;
        }

        #form h3 a:hover{
            color:#FF9900;}

        #row{
            float:left;
            margin-left:30px;
            margin-top:28px;
            background-color:#FFFFFF;}
        @charset "UTF-8";
        /* CSS Document */


        body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, form, fieldset, input, textarea, p, blockquote, th, td {
            margin:0;
            padding:0;
        }
        table {
            border-collapse:collapse;
            border-spacing:0;
        }
        fieldset, img {
            border:0;
        }
        address, caption, cite, code, dfn, em, strong, th, var {
            font-style:normal;
            font-weight:normal;
        }
        ol, ul {
            list-style:none;
        }
        caption, th {
            text-align:left;
        }
        h1, h2, h3, h4, h5, h6 {
            font-size:100%;
            font-weight:normal;
        }
        q:before, q:after {
            content:'';
        }
        abbr, acronym {
            border:0;
        }
        @charset "UTF-8";
        /* CSS Document */

        #login{
            width:240px;
            height:143px;
            border:1px dotted #999999;
        }

        .inputLogin{
            width:145px;
            background:#FAFAFA none repeat scroll 0 0;
            border:1px solid #999999;
            height:18px;
            margin-left:10px;
            display:inline;
        }

        *html .inputLogin{
            width:147px;
            w/idth:145px;
            display:inline;}

        #login label{
            display:block;
            float:left;
            width:58px;
            text-align:left;
            color:#333333;
            font-size:0.9em;
            margin-left:8px;}


        *html #login label{
            width:66px;
            w/idth:58px;
            display:inline;
        }

        #login p{
            margin-top:15px;
            font-family:Arial, Helvetica, sans-serif;
            font-size:0.9em;}

        #login p.top{
            margin-top:25px;}

        .decorativeSubmit{
            margin-left:130px;
            color:#FF6633;
            text-align:center;
            width:50px;
            height: 25px;
            border:2px solid #666666;
            font-size:1em;
            background-color:#FFFFFF;
        }

        /* forgotPassword CSS*/
        #forgotpasswd{
            border:1px dotted #999999;
            height:100px;
            margin-right:20px;
            margin-top:10px;
            width:238px;
            display:none;
        }

        #forgotpasswd label{
            display:block;
            float:left;
            width:60px;
            text-align:left;
            color:#333333;
            font-size:0.9em;
            margin-left:8px;
        }

        #forgotpasswd p{
            margin-top:15px;
            font-family:Arial, Helvetica, sans-serif;
            font-size:0.9em;}

        #forgotpasswd p.top{
            margin-top:25px;}

    </style>
</head>

<body>
<div id="top"></div>
<div id="container">
	<div id="header">
    	<div id="logo"><img src="/images/rextec_logo.png"/></div>
        <div id="title">
        	<h1>
                <span id="appId" style="color:#FF3300"></span>
                <span id="appName"></span>
            </h1>
        </div>
    </div>
