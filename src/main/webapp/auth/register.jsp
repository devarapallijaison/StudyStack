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
    <title>Create account | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/auth.css">
</head>
<body>
<main class="auth-shell">
    <div class="auth-top"><a class="brand" href="<%= request.getContextPath() %>/"><span class="brand-mark">S</span>StudyStack</a><button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch theme">☾</button></div>
    <section class="auth-card" aria-labelledby="page-title">
        <h1 id="page-title">Create your account</h1><p class="intro">Build a practice habit, one mission at a time.</p>
        <% if (error != null) { %><div class="alert error" role="alert"><%= HtmlUtil.escape(error) %></div><% } %>
        <form method="post" action="<%= request.getContextPath() %>/auth/register">
            <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
            <label for="fullName">Full name<input id="fullName" name="fullName" autocomplete="name" required maxlength="100"></label>
            <label for="username">Username<input id="username" name="username" autocomplete="username" required minlength="3" maxlength="30" pattern="[A-Za-z0-9_]+"></label>
            <label for="email">Email<input id="email" name="email" type="email" autocomplete="email" required maxlength="254"></label>
            <label for="password">Password<input id="password" name="password" type="password" autocomplete="new-password" required minlength="10" maxlength="128" aria-describedby="password-hint"><span class="admin-note" id="password-hint">At least 10 characters, including a letter and a number.</span></label>
            <label for="confirmPassword">Confirm password<input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password" required minlength="10" maxlength="128"></label>
            <button class="button" type="submit">Create account</button>
        </form>
        <p class="auth-foot">Already registered? <a href="<%= request.getContextPath() %>/auth/login.jsp">Sign in</a></p>
    </section>
</main>
</body>
</html>
