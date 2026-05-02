package com.scrumcat.service;

import com.scrumcat.vo.BurndownVO;

public interface BurndownService {

    BurndownVO getBurndown(Long sprintId);
}
