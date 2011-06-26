<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <script type="text/javascript">
        function callback() {
        	var url = '<%= request.getParameter("_url")%>';
        	var method = '<%= request.getParameter("_method")%>';
			parent.ZDA.contextLoginPanel.hide();
        }
        </script>
    </head>
    <body onload='callback();'></body>
</html>