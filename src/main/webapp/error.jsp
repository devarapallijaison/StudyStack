<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.RequestDispatcher" %>
<!doctype html>
<html lang="en" data-theme="dark">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Something went wrong | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><main class="page-shell"><div class="card"><div class="eyebrow">Request unavailable</div><h1>Something went wrong.</h1><p>We couldn’t complete that request. Please try again in a moment.</p><a class="button primary" href="<%= request.getContextPath() %>/">Return to StudyStack</a></div></main><jsp:include page="/includes/footer.jsp"/></body>
</html>
