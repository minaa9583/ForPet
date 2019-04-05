package com.forpet.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.forpet.model.vo.BoardSearch;
import com.forpet.model.vo.Image;
import com.forpet.model.vo.Notice;

@Repository
public class NoticeAndEventDaoImpl implements NoticeAndEventDao {
	
	@Autowired
	private SqlSessionTemplate session;

	@Override
	public int noticeCount(BoardSearch bs) {
		return session.selectOne("noticeandevent.noticeCount",bs);
	}

	@Override
	public List<Notice> noticeList(BoardSearch bs) {
		bs.parsing();
		RowBounds row=new RowBounds((bs.getcPageNo()-1)*bs.getNumPerPageNo(), bs.getNumPerPageNo());
		return session.selectList("noticeandevent.noticeList",bs,row);
	}

	@Override
	public int insertNotice(Notice notice) {
		return session.insert("noticeandevent.insertNotice",notice);
	}
	
	@Override
	public int insertImage(Image image) {
		return session.insert("noticeandevent.insertImage",image);
	}

	@Override
	public List<String> noticeImageList(int viewNo) {
		return session.selectList("noticeandevent.noticeImageList",viewNo);
	}

	
	
}
