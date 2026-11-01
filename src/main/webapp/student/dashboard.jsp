<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.studystack.model.User,com.studystack.model.Mission,com.studystack.model.MissionSubmission,com.studystack.util.HtmlUtil" %>
<%
    User user = (User) request.getAttribute("user");
    List<MissionSubmission> activity = (List<MissionSubmission>) request.getAttribute("recentActivity");
    List<Mission> recommended = (List<Mission>) request.getAttribute("recommendedMissions");
    long completedCount = (Long) request.getAttribute("completedCount");
    int availableCount = (Integer) request.getAttribute("availableCount");
    int progress = user.getXpIntoLevel();
%>
<!doctype html>
<html lang="en" data-theme="dark">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Dashboard | StudyStack</title><script src="<%= request.getContextPath() %>/assets/js/theme.js"></script><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css"></head>
<body><jsp:include page="/includes/header.jsp"/><div class="app-layout"><jsp:include page="/includes/sidebar.jsp"/><main class="content">
    <div class="page-head"><div class="eyebrow">Your learning space</div><h1>Welcome back, <%= HtmlUtil.escape(user.getFullName()) %>.</h1><p>Ready to improve your programming skills?</p></div>
    <section class="grid stats-grid" aria-label="Your statistics">
        <article class="card"><div class="stat-label">Total XP</div><div class="stat-value"><%= user.getTotalXp() %></div><div class="xp">earned so far</div></article>
        <article class="card"><div class="stat-label">Current level</div><div class="stat-value"><%= user.getLevel() %></div><div class="xp">Level <%= user.getLevel() %></div></article>
        <article class="card"><div class="stat-label">Completed missions</div><div class="stat-value"><%= completedCount %></div><div class="muted">correctly completed</div></article>
        <article class="card"><div class="stat-label">Available missions</div><div class="stat-value"><%= availableCount %></div><div class="muted">ready when you are</div></article>
    </section>
    <section class="section card">
        <div class="section-head"><div><div class="eyebrow">Level <%= user.getLevel() %></div><h2>Progress to next level</h2></div><span class="xp"><%= user.getXpToNextLevel() %> XP to go</span></div>
        <div class="progress-track" role="progressbar" aria-label="Progress to next level" aria-valuenow="<%= progress %>" aria-valuemin="0" aria-valuemax="100"><div class="progress-fill" style="width:<%= progress %>%"></div></div>
        <div class="progress-meta"><span><%= progress %> / 100 XP in this level</span><span><%= user.getTotalXp() %> total XP</span></div>
    </section>
    <section class="section"><div class="section-head"><h2>Recommended missions</h2><a href="<%= request.getContextPath() %>/student/missions">Explore all →</a></div>
        <% if (recommended.isEmpty()) { %><div class="empty">You’ve attempted every available mission. New challenges will appear here.</div><% } else { %>
        <div class="grid mission-grid"><% for (Mission mission : recommended) { %><article class="card mission-card"><span class="eyebrow"><%= HtmlUtil.escape(mission.getCategory()) %></span><h3><%= HtmlUtil.escape(mission.getTitle()) %></h3><p><%= HtmlUtil.escape(mission.getDescription()) %></p><div class="card-bottom"><span class="badge"><%= HtmlUtil.escape(mission.getDifficulty()) %></span><span class="xp">+<%= mission.getXpReward() %> XP</span><a class="button" href="<%= request.getContextPath() %>/student/mission?id=<%= mission.getMissionId() %>">Open mission</a></div></article><% } %></div><% } %>
    </section>
    <section class="section"><div class="section-head"><h2>Recent activity</h2><a href="<%= request.getContextPath() %>/student/profile">View profile →</a></div>
        <% if (activity.isEmpty()) { %><div class="empty">Your completed missions and XP awards will show up here.</div><% } else { %><article class="card"><% for (MissionSubmission item : activity) { %><div class="activity-row"><div><strong><%= HtmlUtil.escape(item.getMissionTitle()) %></strong><div class="muted"><%= item.getSubmittedAt() == null ? "" : item.getSubmittedAt().toString().replace('T',' ') %></div></div><span class="<%= item.isCorrect() ? "xp" : "muted" %>"><%= item.isCorrect() ? "+" + item.getXpEarned() + " XP" : "Attempted" %></span></div><% } %></article><% } %>
    </section>
</main></div><jsp:include page="/includes/footer.jsp"/></body></html>
