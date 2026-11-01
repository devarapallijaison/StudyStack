<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.studystack.util.CsrfToken,com.studystack.util.HtmlUtil" %>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) session.getAttribute("success");
    session.removeAttribute("success");
    String csrfToken = CsrfToken.getOrCreate(session);
%>
<!doctype html>
<html lang="en" data-theme="dark">
<head>
    <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/auth.css">
</head>
<body>
<main class="auth-shell">
    <div class="auth-top"><a class="brand" href="<%= request.getContextPath() %>/"><span class="brand-mark">S</span>StudyStack</a><button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch theme">☾</button></div>
    <section class="auth-card" aria-labelledby="page-title">
        <h1 id="page-title">Welcome back</h1><p class="intro">Sign in and pick up where you left off.</p>
        <% if (error != null) { %><div class="alert error" role="alert"><%= HtmlUtil.escape(error) %></div><% } %>
        <% if (success != null) { %><div class="alert success" role="status"><%= HtmlUtil.escape(success) %></div><% } %>
        <form method="post" action="<%= request.getContextPath() %>/auth/login">
            <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
            <label for="identifier">Username or email<input id="identifier" name="identifier" autocomplete="username" required maxlength="254"></label>
            <label for="password">Password<input id="password" name="password" type="password" autocomplete="current-password" required maxlength="128"></label>
            <button class="button" type="submit">Sign in</button>
        </form>
        <p class="auth-foot">New to StudyStack? <a href="<%= request.getContextPath() %>/auth/register.jsp">Create an account</a></p>
    </section>
</main>
</body>
</html>
