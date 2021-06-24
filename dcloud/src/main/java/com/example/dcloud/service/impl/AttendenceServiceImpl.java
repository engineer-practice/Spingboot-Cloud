package com.example.dcloud.service.impl;

import com.example.dcloud.entity.Attendence;
import com.example.dcloud.mapper.AttendenceMapper;
import com.example.dcloud.service.AttendenceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AttendenceServiceImpl extends ServiceImpl<AttendenceMapper, Attendence> implements AttendenceService {

}
