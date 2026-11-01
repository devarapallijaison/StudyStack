<%@ page import="com.studystack.util.CsrfToken,com.studystack.util.HtmlUtil" %>
<%
    String headerRole = (String) session.getAttribute("ROLE");
    String headerName = (String) session.getAttribute("USERNAME");
    String headerCsrf = CsrfToken.getOrCreate(session);
%>
<header class="topbar">
    <a class="brand" href="<%= request.getContextPath() %>/"><span class="brand-mark">S</span>StudyStack</a>
    <nav class="topnav" aria-label="Main navigation">
        <% if ("STUDENT".equals(headerRole)) { %>
            <a class="optional" href="<%= request.getContextPath() %>/student/missions">Missions</a>
            <a class="optional" href="<%= request.getContextPath() %>/student/leaderboard">Leaderboard</a>
            <a href="<%= request.getContextPath() %>/student/profile"><%= HtmlUtil.escape(headerName) %></a>
            <form class="logout-form" method="post" action="<%= request.getContextPath() %>/logout">
                <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(headerCsrf) %>">
                <button class="button" type="submit">Log out</button>
            </form>
        <% } else if ("ADMIN".equals(headerRole)) { %>
            <a href="<%= request.getContextPath() %>/admin/dashboard">Admin</a>
            <a class="optional" href="<%= request.getContextPath() %>/admin/missions">Missions</a>
            <form class="logout-form" method="post" action="<%= request.getContextPath() %>/logout">
                <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(headerCsrf) %>">
                <button class="button" type="submit">Log out</button>
            </form>
        <% } else { %>
            <a class="optional" href="<%= request.getContextPath() %>/admin/login.jsp">Admin</a>
            <a class="button" href="<%= request.getContextPath() %>/auth/login.jsp">Sign in</a>
        <% } %>
        <button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch theme">☾</button>
    </nav>
</header>
