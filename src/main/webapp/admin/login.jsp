<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.studystack.util.CsrfToken,com.studystack.util.HtmlUtil" %>
<%
    String error = (String) request.getAttribute("error");
    String csrfToken = CsrfToken.getOrCreate(session);
%>
<!doctype html>
<html lang="en" data-theme="dark">
<head>
    <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin sign in | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/auth.css">
</head>
<body>
<main class="auth-shell">
    <div class="auth-top"><a class="brand" href="<%= request.getContextPath() %>/"><span class="brand-mark">S</span>StudyStack</a><button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch theme">☾</button></div>
    <section class="auth-card" aria-labelledby="page-title">
        <h1 id="page-title">Administrator sign in</h1><p class="intro">Manage missions and keep the learning library current.</p>
        <% if (error != null) { %><div class="alert error" role="alert"><%= HtmlUtil.escape(error) %></div><% } %>
        <form method="post" action="<%= request.getContextPath() %>/admin/auth">
            <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
            <label for="username">Username<input id="username" name="username" autocomplete="username" required maxlength="50"></label>
            <label for="password">Password<input id="password" name="password" type="password" autocomplete="current-password" required maxlength="128"></label>
            <button class="button" type="submit">Sign in to admin</button>
        </form>
        <p class="auth-foot"><a href="<%= request.getContextPath() %>/auth/login.jsp">Return to student sign in</a></p>
    </section>
</main>
</body>
</html>
