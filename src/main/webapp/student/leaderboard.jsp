<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.studystack.model.User,com.studystack.util.HtmlUtil" %>
<% List<User> leaders = (List<User>) request.getAttribute("leaders"); int currentUserId = (Integer) request.getAttribute("currentUserId"); %>
<!doctype html><html lang="en" data-theme="dark"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Leaderboard | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/sidebar.jsp"/><main class="content">
    <div class="page-head"><div class="eyebrow">Community progress</div><h1>Leaderboard</h1><p>Students are ranked by XP, then completed missions, then username.</p></div>
    <% if (leaders.isEmpty()) { %><div class="empty">The leaderboard will appear after students earn XP.</div><% } else { %><div class="table-wrap"><table><thead><tr><th>Rank</th><th>Student</th><th>Level</th><th>Completed</th><th>Total XP</th></tr></thead><tbody>
        <% for (User leader : leaders) { %><tr class="<%= leader.getUserId() == currentUserId ? "current-user" : "" %>"><td><%= leader.getLeaderboardRank() %></td><td><strong><%= HtmlUtil.escape(leader.getUsername()) %></strong><%= leader.getUserId() == currentUserId ? " · You" : "" %></td><td><%= leader.getLevel() %></td><td><%= leader.getCompletedMissions() %></td><td class="xp"><%= leader.getTotalXp() %></td></tr><% } %>
    </tbody></table></div><% } %>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
