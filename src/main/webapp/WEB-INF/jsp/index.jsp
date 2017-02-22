<%@include file="inc/head.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${name} (${version})</title>

    <meta charset="utf-8">

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />

    <link rel="stylesheet" href="<c:url value="/jquery/jquery.mobile-1.4.2.min.css" />" />

    <script src="<c:url value="/jquery/jquery-1.9.1.min.js" />"></script>
    <script src="<c:url value="/jquery/jquery.mobile-1.4.2.min.js" />"></script>
    <script src="<c:url value="/jquery/jquery.i18n.properties.js" />"></script>
    <script src="<c:url value="/jquery/jquery.textchange.min.js" />"></script>
    <script src="<c:url value="/ad-password-handler-web/ad-password-handler-web.js" />"></script>

    <script type="text/javascript">
        var Settings = {
            name : '${name}',
            version : '${version}',
            bundlePath : '<c:url value="/ad-password-handler-web/bundle/" />'
        };
    </script>
</head>
<body>
<%-- page is created dynamically with jquery mobile --%>
</body>
</html>