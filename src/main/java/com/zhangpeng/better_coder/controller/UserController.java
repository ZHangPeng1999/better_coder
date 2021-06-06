package com.zhangpeng.better_coder.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zhangpeng.better_coder.component.BaiDuAIComponent;
import com.zhangpeng.better_coder.component.EncryptComponent;
import com.zhangpeng.better_coder.component.MyToken;
import com.zhangpeng.better_coder.component.RequestComponent;
import com.zhangpeng.better_coder.entity.*;
import com.zhangpeng.better_coder.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.swing.event.InternalFrameEvent;
import javax.websocket.server.PathParam;
import java.util.*;

@RestController
@RequestMapping("/api/")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EncryptComponent encryptComponent;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ExamService examService;
    @Autowired
    private DocService docService;
    @Autowired
    private RequestComponent requestComponent;
    @Value("${my.user}")
    private String roleUser;
    @Value("${my.admin}")
    private String roleAdmin;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BaiDuAIComponent baiDuAIComponent;

    @PostMapping("sign_in")
    public Map SignIn(@RequestBody Map<String, String> signIn, HttpServletResponse response) {
        User user = Optional.ofNullable(userService.getUserByNum(Integer.valueOf(signIn.get("username"))))
                .filter(u -> signIn.get("password").equals(u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));
        MyToken myToken = new MyToken(user.getId(), user.getRole());
        String auth = encryptComponent.encryptToken(myToken);
        response.setHeader(MyToken.AUTHORIZATION, auth);
//        token中包含用户身份 但是前端不能通过解密token得到身份 因此通过编码用户身份传递给前端 编码后传递给前端增加安全性
        String roleCode = user.getRole() == User.Role.ADMIN ? roleAdmin : roleUser;
//        KeyPairGenerator.getInstance("RSA").generateKeyPair(); 获得密钥对
        return Map.of("role", roleCode, "name", user.getName());
    }

    @PostMapping("sign_up")
    public Map SignUp(@RequestBody Map<String, String> signUp) {
        Integer userName = Integer.valueOf(signUp.get("username"));
        String name = signUp.get("name");
        String password = signUp.get("password");
        userService.addUser(name, password, User.Role.USER, userName);
        return Map.of("message", "注册成功");
    }

    @PostMapping("search_user")
    public Map SearchUser(@RequestBody Map<String, String> searchUser) {
        String name = searchUser.get("name");
        String numStr = searchUser.get("username");
        Integer num = 0;
        if (numStr.length() != 0) {
            num = Integer.valueOf(numStr);
        }
        Integer offset = Integer.valueOf(searchUser.get("offset"));
        Integer limit = Integer.valueOf(searchUser.get("limit"));
        return userService.searchUser(num, name, offset, limit);
    }

    @PostMapping("update_user")
    public Map UpdateUser(@RequestBody Map<String, String> updateUser) {
        String name = updateUser.get("name");
        String password = updateUser.get("password");
        String idStr = updateUser.get("id");
        Integer id = Integer.valueOf(idStr);
        String roleStr = updateUser.get("role");
        User user = userService.getUser(id);
        User.Role role = user.getRole();
        if (roleStr.length() != 0) {
            switch (roleStr) {
                case "1":
                    role = User.Role.ADMIN;
                case "2":
                    role = User.Role.USER;
            }
        }
        userService.updateUser(id, name, password, role);
        return Map.of("message", "success");
    }

    @PostMapping("search_course")
    public Map SearchCourse(@RequestBody Map<String, String> searchCourse) {
        log.warn(String.valueOf(searchCourse));
        String title = searchCourse.get("title");
        String content = searchCourse.get("content");
        String typeStr = searchCourse.get("type");
        Integer type = Integer.valueOf(typeStr);
        String statusStr = searchCourse.get("status");
        Integer status = Integer.valueOf(statusStr);
        String bizType = searchCourse.get("biz_type");
        Integer offset = Integer.valueOf(searchCourse.get("offset"));
        Integer limit = Integer.valueOf(searchCourse.get("limit"));
        return courseService.searchCourse(title, content, type, bizType, status, offset, limit);
    }

    @PostMapping("add_course")
    public Map AddCourse(@RequestBody Map<String, String> addCourse) {
        String title = addCourse.get("title");
        String content = addCourse.get("content");
        String typeStr = addCourse.get("type");
        Integer type = Integer.valueOf(typeStr);
        String statusStr = addCourse.get("status");
        Integer status = Integer.valueOf(statusStr);
        String bizType = addCourse.get("biz_type");
        String sourceUri = addCourse.get("source_uri");
        String projectUri = addCourse.get("project_uri");
        Course course = courseService.addCourse(title, content, type, bizType, sourceUri, projectUri,status);
        return Map.of("message", "success", "course", course);
    }

    @PostMapping("update_course")
    public Map UpdateCourse(@RequestBody Map<String, String> addCourse) {
        String title = addCourse.get("title");
        String content = addCourse.get("content");
        String typeStr = addCourse.get("type");
        Integer type = Integer.valueOf(typeStr);
        String statusStr = addCourse.get("status");
        Integer status = Integer.valueOf(statusStr);
        String cidStr = addCourse.get("cid");
        Integer cid = Integer.valueOf(cidStr);
        String bizType = addCourse.get("biz_type");
        String sourceUri = addCourse.get("source_uri");
        String projectUri = addCourse.get("project_uri");
        Course course = courseService.updateCourse(cid, title, content, type, bizType, sourceUri, projectUri, status);
        return Map.of("message", "success", "course", course);
    }

    @GetMapping("course/{cid}")
    public Map GetCourse(@PathVariable Integer cid) {
        Boolean isSelect = false;
        Integer uid = requestComponent.getUid();
        User user = userService.getUser(uid);
        ChooseCourse myChooseCourse = new ChooseCourse();

        for (ChooseCourse chooseCours : user.getChooseCourses()) {
            if (chooseCours.getCourse().getId() == cid) {
                isSelect = true;
                myChooseCourse = chooseCours;
                break;
            }
        }

        return Map.of("course", courseService.getCourse(cid), "chapters", courseService.getChapters(cid), "is_select", isSelect, "chooseCourse", myChooseCourse);
    }

    @GetMapping("chapter/{id}")
    public Map GetChapter(@PathVariable Integer id) {
        Chapter chapter = courseService.getChapter(id);
        Course course = chapter.getCourse();
        Integer uid = requestComponent.getUid();
        courseService.readChapter(id, course.getId(), uid);
        return Map.of("chapter", chapter, "path", "//player.bilibili.com/player.html?aid=63172872&bvid=" + chapter.getVideoId() + "&cid=110381387&page=1");
    }

    @PostMapping("select_course")
    public Map SelectCourse(@RequestBody Map<String, String> select_course) {
        Integer id = Integer.valueOf(select_course.get("id"));
        Integer uid = requestComponent.getUid();
        courseService.selectCourse(uid, id);
        return Map.of("message", "success");
    }

    @PostMapping("add_chapter")
    public Map AddChapter(@RequestBody Map<String, String> add_chapter) {
        Integer cId = Integer.valueOf(add_chapter.get("cid"));
        String title = add_chapter.get("title");
        String content = add_chapter.get("content");
        String video_id = add_chapter.get("video_id");
        Integer orderId = Integer.valueOf(add_chapter.get("order_id"));
        courseService.addChapter(cId, title, content, orderId, video_id);
        return Map.of("message", "success");
    }

    @PostMapping("update_chapter")
    public Map UpdateChapter(@RequestBody Map<String, String> add_chapter) {
        log.error(String.valueOf(add_chapter));
        Integer id = Integer.valueOf(add_chapter.get("id"));
        String title = add_chapter.get("title");
        String content = add_chapter.get("content");
        String video_id = add_chapter.get("video_id");
        Integer orderId = Integer.valueOf(add_chapter.get("order_id"));
        courseService.updateChapter(id, title, content, 0, orderId, video_id);
        return Map.of("message", "success");
    }

    @PostMapping("search_question")
    public Map SearchQuestion(@RequestBody Map<String, String> search_question) {
        String title = search_question.get("title");
        String content = search_question.get("content");
        String typeStr = search_question.get("type");
        Integer type = Integer.valueOf(typeStr);
        String levelStr = search_question.get("level");
        Integer level = Integer.valueOf(levelStr);
        String statusStr = search_question.get("status");
        Integer status = Integer.valueOf(statusStr);
        String bizType = search_question.get("biz_type");
        Integer offset = Integer.valueOf(search_question.get("offset"));
        Integer limit = Integer.valueOf(search_question.get("limit"));
        return questionService.searchQuestion(title, content, bizType, level,status, type, offset, limit);
    }

    @PostMapping("add_question")
    public Map AddQuestion(@RequestBody Map<String, String> add_question) {
        log.error(String.valueOf(add_question));
        String title = add_question.get("title");
        String content = add_question.get("content");
        String typeStr = add_question.get("type");
        Integer type = Integer.valueOf(typeStr);
        String levelStr = add_question.get("level");
        Integer level = Integer.valueOf(levelStr);
        String statusStr = add_question.get("status");
        Integer status = Integer.valueOf(statusStr);
        String bizType = add_question.get("biz_type");
        String answer = add_question.get("answer");
        String a = add_question.get("a");
        String b = add_question.get("b");
        String c = add_question.get("c");
        String d = add_question.get("d");
        switch (type) {
            case 1:
                questionService.addSelectQuestion(title, content, bizType, level, status, answer, a, b, c, d);
                break;
            case 2:
                questionService.addJudgeQuestion(title, content, bizType, level, status, answer, a, b);
                break;
            case 3:
                questionService.addAnalysisQuestion(title, content, bizType, status, level, answer);
                break;
        }
        return Map.of("message", "success");
    }

    @PostMapping("update_question")
    public Map UpdateQuestion(@RequestBody Map<String, String> updateQuestion) {
        Integer id = Integer.valueOf(updateQuestion.get("id"));
        String title = updateQuestion.get("title");
        String content = updateQuestion.get("content");
        String bizType = updateQuestion.get("biz_type");
        Integer level = Integer.valueOf(updateQuestion.get("level"));
        String statusStr = updateQuestion.get("status");
        Integer status = Integer.valueOf(statusStr);
        String answer = updateQuestion.get("answer");
        String a = updateQuestion.get("a");
        String b = updateQuestion.get("b");
        String c = updateQuestion.get("c");
        String d = updateQuestion.get("d");
        questionService.updateQuestion(id, title, content, bizType, level, status, answer, a, b, c, d);
        return Map.of("message", "success");
    }
    @GetMapping("question/{id}")
    public Map GetQuestion(@PathVariable Integer id) {
        return Map.of("question", questionService.getQuestion(id));
    }
    @GetMapping("index")
    public Map GetIndex() {
        Integer uid = requestComponent.getUid();
        User user = userService.getUser(uid);
        return Map.of("user", user, "docs", user.getDocs(), "question_history", user.getChooseQuestions(),"courses",user.getChooseCourses());
    }

    @PostMapping("search_doc")
    public Map SearchDoc(@RequestBody Map<String, String> searchDoc) {
        String title = searchDoc.get("title");
        String content = searchDoc.get("content");
        Integer type = Integer.valueOf(searchDoc.get("type"));
        Integer offset = Integer.valueOf(searchDoc.get("offset"));
        Integer limit = Integer.valueOf(searchDoc.get("limit"));
        return docService.searchDoc(title, content, type, 2, offset, limit);
    }

    @GetMapping("get_doc/{id}")
    public Map GetDoc(@PathVariable Integer id) {
        return Map.of("doc", docService.getDoc(id),"name",docService.getDoc(id).getUser().getName());
    }

    @GetMapping("get_docs/{type}")
    public Map GetDocs(@PathVariable Integer type) {
        Integer uid = requestComponent.getUid();
        User user = userService.getUser(uid);
        List<Doc> docs = new ArrayList<>();
        for (Doc doc : user.getDocs()) {
            if (doc.getType() == type){
                docs.add(doc);
            }
        }
        return Map.of("docs", docs);
    }

    @PostMapping("add_doc")
    public Map AddDoc(@RequestBody Map<String, String> addDoc) {
        String title = addDoc.get("title");
        String content = addDoc.get("content");
        Integer type = Integer.valueOf(addDoc.get("type"));
        Integer status = Integer.valueOf(addDoc.get("status"));
        Integer uid = requestComponent.getUid();
        docService.addDoc(uid, type, title, content, status);
        return Map.of("message", "success");
    }

    @PostMapping("update_doc")
    public Map UpdateDoc(@RequestBody Map<String, String> updateDoc) {
        String title = updateDoc.get("title");
        String content = updateDoc.get("content");
        Integer type = Integer.valueOf(updateDoc.get("type"));
        Integer id = Integer.valueOf(updateDoc.get("id"));
        Integer status = Integer.valueOf(updateDoc.get("status"));
        docService.updateDoc(id, type, title, content, status);
        return Map.of("message", "success");
    }
    @PostMapping("answer_question")
    public Map AnswerQuestion(@RequestBody Map<String, String> answerQuestion) {
        Integer uid = requestComponent.getUid();
        Integer id = Integer.valueOf(answerQuestion.get("id"));
        String result = answerQuestion.get("result");
        return Map.of("result",questionService.judgeQuestion(result, id, uid));
    }
    @GetMapping("exams")
    public Map GetExams() {
        Integer uid = requestComponent.getUid();
        User user = userService.getUser(uid);
        return Map.of("exams", user.getExams());
    }
    @GetMapping("exam/{id}")
    public Map GetExams(@PathVariable Integer id) {
        List<ChooseQuestion> chooseQuestions = new ArrayList<>();
        for (ChooseQuestion chooseQuestion : examService.getExam(id).getChooseQuestions()) {
            chooseQuestions.add(chooseQuestion);
        }
        return Map.of("exam", examService.getExam(id), "choose_questions",chooseQuestions);
    }
    @PostMapping("begin_exam")
    public Map BeginExam(@RequestBody Map<String, String> beginExam) {
        Integer uid = requestComponent.getUid();
        String bizType = beginExam.get("biz_type");
        if (bizType.length() == 0) {
            bizType = "%";
        }
        Integer level = Integer.valueOf(beginExam.get("level")) ;
        return Map.of("exam",examService.addExam(uid, bizType, level));
    }

    @PostMapping("judge_exam")
    public Map JudgeExam(@RequestBody Map<String, String> judge_exam) {
//        log.error(judge_exam.get("choose_questions"));
        List<JudgeExam> judge_exams = JSON.parseArray(judge_exam.get("choose_questions"), JudgeExam.class);
        Integer id = Integer.valueOf((String) judge_exam.get("id")) ;
        List<ChooseQuestion> chooseQuestions = new ArrayList<>();
        for (JudgeExam judgeExam : judge_exams) {
           ChooseQuestion chooseQuestion = questionService.getChooseQuestion(Integer.valueOf(judgeExam.id));
           chooseQuestion.setAnswer(judgeExam.getAnswer());
           chooseQuestions.add(chooseQuestion);
        }
        Exam exam = examService.checkExam(id, chooseQuestions);
        return Map.of("exam", exam, "choose_questions", exam.getChooseQuestions());
     }
     @PostMapping("begin_interview")
     public Map BeginInterview(@RequestBody Map<String, String> beginInterview){

         Integer uid = requestComponent.getUid();
         String bizType = beginInterview.get("biz_type");
         if (bizType.length() == 0) {
             bizType = "%";
         }
         Integer level = Integer.valueOf(beginInterview.get("level")) ;
         return Map.of("exam",examService.addInterview(uid, bizType, level));
     }
     @GetMapping("boards")
     public  Map GetBoards(){
        return Map.of("boards", boardService.getNewBoardList());
     }
     @PostMapping("get_text")
     public Map GetText(@RequestBody Map<String, String> get_text) {
        String data = get_text.get("data");
        log.error(data.split(",")[1]);
        String res = baiDuAIComponent.VoiceToString(data.getBytes() );
        log.error(res);
        return Map.of("message","success","res",res);
     }
     @Autowired
     MenuService menuService;
    @GetMapping("get_menus")
    public  Map GetMenus(){
        return Map.of("menus", menuService.getAllMenu());
    }
    @PostMapping("update_menu")
    public Map UpdateMenu(@RequestBody Map<String,String>updateMenu) {
        Integer id = Integer.valueOf( updateMenu.get("id")) ;
        Integer open = Integer.valueOf( updateMenu.get("open"));
        menuService.updateMenu(id, open);
        return Map.of("message","success");
    }
}
