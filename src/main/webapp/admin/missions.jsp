<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.studystack.model.Mission,com.studystack.util.CsrfToken,com.studystack.util.HtmlUtil" %>
<%
    List<Mission> missions = (List<Mission>) request.getAttribute("missions");
    String flash = (String) session.getAttribute("adminFlash"); session.removeAttribute("adminFlash");
    String csrfToken = CsrfToken.getOrCreate(session);
%>
<!doctype html><html lang="en" data-theme="dark"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Mission management | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/admin-sidebar.jsp"/><main class="content">
    <div class="page-head"><div class="eyebrow">Administration</div><h1>Mission management</h1><p>Edit questions before students attempt them. Archive missions to preserve their submission history.</p><a class="button primary" href="<%= request.getContextPath() %>/admin/missions/new">Create mission</a></div>
    <% if (flash != null) { %><div class="alert success" role="status"><%= HtmlUtil.escape(flash) %></div><% } %>
    <% if (missions.isEmpty()) { %><div class="empty">No missions yet. Create the first one to start the library.</div><% } else { %><div class="table-wrap"><table><thead><tr><th>Mission</th><th>Category</th><th>Difficulty</th><th>Reward</th><th>Status</th><th>Actions</th></tr></thead><tbody>
        <% for (Mission mission : missions) { %><tr><td><strong><%= HtmlUtil.escape(mission.getTitle()) %></strong></td><td><%= HtmlUtil.escape(mission.getCategory()) %></td><td><%= HtmlUtil.escape(mission.getDifficulty()) %></td><td class="xp"><%= mission.getXpReward() %> XP</td><td><span class="badge <%= mission.isActive() ? "success" : "" %>"><%= mission.isActive() ? "Active" : "Archived" %></span></td><td><% if (mission.isActive()) { %><a href="<%= request.getContextPath() %>/admin/missions?edit=<%= mission.getMissionId() %>">Edit</a><form method="post" action="<%= request.getContextPath() %>/admin/mission/archive" style="display:inline" onsubmit="return confirm('Archive this mission? Existing student history will be preserved.');"><input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>"><input type="hidden" name="missionId" value="<%= mission.getMissionId() %>"><button class="button" type="submit">Archive</button></form><% } else { %><span class="muted">History preserved</span><% } %></td></tr><% } %>
    </tbody></table></div><% } %>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
