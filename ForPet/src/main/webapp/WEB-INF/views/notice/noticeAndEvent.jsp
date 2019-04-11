<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="${path }/resources/css/boardStyle.css"/>

<section>
	<div class="noticeAndEvent-wrapper">
		<div class="board-wrapper">
			<div class="board-title">
				<p>공지사항</p>
			</div>
			<div class="board-table">
				<div class="table-header">
					<div>글번호</div>
					<div>제목</div>
					<div>작성자</div>
					<div>조회수</div>
					<div>작성일</div>
				</div>
				<c:forEach items="${nlist}" var="n">
				<div class="table-row">
					<div>${n.noticeSeq}</div>
					<div class="board-data-title" onclick="noticeView(${n.noticeSeq})">${n.noticeTitle}</div>
					<div class="userBox">${n.memberNickname}</div>
					<div>${n.readCount}</div>
					<div>${n.noticeDate}</div>
				</div>
				</c:forEach>
			</div>
			<div class="board-search-menu">
		    	<div>
			    	<input type="text" id="board-search-text" value="" onkeydown="fn_search_write()" >
			    	<input type="button" value="검색" onclick="fn_board_search()">
		    	</div>
		    	<div></div>
		    	<div></div>
		    	<div>
		    		<c:if test='${loggedMember!=null&&"admin" eq loggedMember.memberEmail}'>
					<button class="board-write" onclick="location.href='${path}/notice/noticeForm';">글쓰기</button>
		            </c:if>
		    	</div>
		    </div>
			${npage}
		</div> 
		<div class="board-wrapper">
			<div class="board-title">
				<p>이벤트</p>
			</div>
					<select name="category" id="category" style="margin:10px;height:30px;width:200px">
							<option value="">진행중인 이벤트</option>
							<option value="">종료된 이벤트</option>
							<option value="">모든 이벤트</option>
					</select>
			<div class="board-preview">
				<c:forEach items="${elist}" var="e">
				<div class="board-preview-element" onclick="location.href='${path}/event/eventList?viewNo=${e.eventSeq}'">
					<img class="board-preview-image" src="${path}/resources/upload/eventImage/${e.filename}"/>
					<div class="board-preview-title">${e.eventTitle}</div>
					<div class="board-preview-date">${e.eventStart}부터 ${e.eventFinish}까지</div>
				</div>
				</c:forEach>
			</div>
			<div class="board-search-menu">
				<div>
		 			<input type="text" id="board-e-search-text" value="" onkeydown="fn_e_search_write()">
					<input type="button" value="검색" onclick="fn_e_board_search()">
				</div>
				<div></div>
				<div></div>
				<div>
					<c:if test='${loggedMember!=null&&"admin" eq loggedMember.memberEmail}'>
						<button class="board-write" onclick="location.href='${path}/event/eventForm';">글쓰기</button>
					</c:if>
				</div>
			</div>
			${epage}
		</div>
	</div>
	<form action="${path}/notice/noticeList" method="post" id="noticeView">
		<input type="hidden" name="viewNo" id="noticeViewNo"/>
	</form>
</section>

<script>
function noticeView(viewNo)
{
	$("#noticeViewNo").val(viewNo);
	$("#noticeView").submit();
}

function fn_board_search(){
	 location.href='${path}/notice/noticeList?keyword='+$("#board-search-text").val();
}
function fn_search_write(){
	if(window.event.keyCode == 13)
	{
		fn_board_search();
	}
}

function fn_e_board_search(){
	 location.href='${path}/event/eventList?keyword='+$("#board-e-search-text").val();
}

function fn_e_search_write(){
	if(window.event.keyCode == 13)
	{
		fn_e_board_search();
	}
}
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
