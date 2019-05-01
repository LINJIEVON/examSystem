package com.sp.exam.controller;

import com.sp.exam.VO.ResultVO;
import com.sp.exam.constant.CookieConstant;
import com.sp.exam.constant.RedisConstant;
import com.sp.exam.dao.UserDao;
import com.sp.exam.pojo.CourseExam;
import com.sp.exam.pojo.TimeTable;
import com.sp.exam.service.*;
import com.sp.exam.utils.CookieUtil;
import com.sp.exam.utils.ResultVOUtil;
import com.sp.exam.utils.UserTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/admin/examArranged")
public class ExamArrangedController {
    @Autowired
    private ArrangedCourseService arrangedCourseService;

    @Autowired
    private ArrangedExamRoomService arrangedExamRoomService;

    @Autowired
    private ArrangedStuSitService arrangedStuSitService;

    @Autowired
    private CourseExamService courseExamService;

    @Autowired
    private RemixCourseService remixCourseService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserDao userDao;

    @GetMapping("/confirmExamCourse")
    public ResultVO confirmExamCourse(){
        List<String> exam = courseExamService.getExam();
        remixCourseService.autoRemix();

        return ResultVOUtil.success();
    }

    @GetMapping("/autoArranged")
    public ModelAndView autoArranged(Map<String,Object> map,
                                     @RequestParam(value = "day",defaultValue = "1")String day,
                                     HttpServletRequest request){
        List<TimeTable> timeTables = arrangedCourseService.timeTableStatus(day);
        List<CourseExam> courseExams=arrangedCourseService.courseExamStatus();
        List<CourseExam> courseExamShows=courseExamService.showCourseExam();
        map.put("timetables",timeTables);
        map.put("courseExams",courseExams);
        map.put("courseExamShows",courseExamShows);
        Cookie cookie= CookieUtil.get(request, CookieConstant.TOKEN);
        String userID = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        map.put("userName",userDao.findById(userID).get().getNickname());
        map.put("userType", UserTypeUtil.userType(userDao.findById(userID).get().getUserType()));
        return new ModelAndView("examArranged/autoExamArranged",map);
    }

    @GetMapping("/auto1")
    public ResultVO auto1(){
        arrangedCourseService.arrangedTimeTableByDay();
        return ResultVOUtil.success();
    }

    @GetMapping("/auto2")
    public ResultVO auto2(){
        arrangedCourseService.arrangedTimeTableByTimeSlot();
        return ResultVOUtil.success();
    }

    @GetMapping("/auto3")
    public ResultVO auto3(){
        arrangedCourseService.arrangedTimeTableByNoting();
        return ResultVOUtil.success();
    }

    @GetMapping("/last")
    public ResultVO last(){
        arrangedExamRoomService.arrangedExamRoom();
        arrangedStuSitService.arrangedStuSit();
        return ResultVOUtil.success();
    }


}
