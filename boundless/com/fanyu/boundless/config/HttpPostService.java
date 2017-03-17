package com.fanyu.boundless.config;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface HttpPostService {
    @FormUrlEncoded
    @POST("addGetLeaveNotice.action")
    Observable<String> AddGetLeaveNotice(@Field("userid") String str, @Field("classid") String str2, @Field("tittle") String str3, @Field("editcontent") String str4, @Field("yijian") String str5, @Field("gstype") String str6, @Field("classname") String str7, @Field("content") String str8, @Field("selectsum") String str9, @Field("selectzu") String str10, @Field("unselectsum") String str11, @Field("studentnamesum") String str12);

    @FormUrlEncoded
    @POST("xjObtainClassMemeber.action")
    Observable<String> ObtainClassMemeber(@Field("classid") String str);

    @FormUrlEncoded
    @POST("PhoneLoginServlet")
    Observable<String> PhoneLoginServlet(@Field("uuid") String str, @Field("uname") String str2, @Field("upwd") String str3);

    @FormUrlEncoded
    @POST("queryVideoAlbumList.action")
    Observable<String> WeikeList(@Field("typeid") String str, @Field("page") String str2, @Field("pagesize") String str3);

    @FormUrlEncoded
    @POST("addChild.action")
    Observable<String> addChild(@Field("student.snickname") String str, @Field("student.ssex") String str2, @Field("student.simg") String str3, @Field("parentid") String str4);

    @FormUrlEncoded
    @POST("addXiaoZu.action")
    Observable<String> addClassZu(@Field("studentid") String str, @Field("zuname") String str2, @Field("classid") String str3);

    @FormUrlEncoded
    @POST("shuoshuosave.action")
    Observable<String> addDongTai(@Field("content") String str, @Field("userid") String str2, @Field("dailytypeid") String str3, @Field("address") String str4, @Field("classid") String str5, @Field("classname") String str6, @Field("biaoqian") String str7, @Field("biaoqianid") String str8);

    @FormUrlEncoded
    @POST("addZuoYeBoBao.action")
    Observable<String> addHomeWork(@Field("hwdescribe") String str, @Field("userid") String str2, @Field("hwtype") String str3, @Field("tittle") String str4, @Field("banjiid") String str5, @Field("xiaozuid") String str6, @Field("gerenid") String str7);

    @FormUrlEncoded
    @POST("addQuestion.action")
    Observable<String> addQuestion(@Field("questioncontent") String str);

    @FormUrlEncoded
    @POST("createSchoolClass.action")
    Observable<String> createClass(@Field("classname") String str, @Field("classimg") String str2, @Field("userid") String str3, @Field("createname") String str4);

    @FormUrlEncoded
    @POST("DeleteClass.action")
    Observable<String> deleteClass(@Field("classid") String str);

    @FormUrlEncoded
    @POST("deleteDongTai.action")
    Observable<String> deleteDongTai(@Field("userid") String str, @Field("dailyid") String str2);

    @FormUrlEncoded
    @POST("deleteGeRenZuoYe.action")
    Observable<String> deleteGeRenZuoYe(@Field("userid") String str, @Field("itemid") String str2);

    @FormUrlEncoded
    @POST("deleteZuoYe.action")
    Observable<String> deleteZuoYe(@Field("userid") String str, @Field("itemid") String str2);

    @FormUrlEncoded
    @POST("getAllClassZuList.action")
    Observable<String> getAllClassZuList(@Field("userid") String str);

    @FormUrlEncoded
    @POST("AppFiftyToneGraph/videoLink")
    Observable<String> getAllVedioBy(@Field("once_no") boolean z);

    @FormUrlEncoded
    @POST("getMyGetLeaveNoticeList.action")
    Observable<String> getArriveOrLeaveList(@Field("page") String str, @Field("pagesize") String str2, @Field("userid") String str3, @Field("classid") String str4, @Field("version") String str5);

    @FormUrlEncoded
    @POST("getMyClass.action")
    Observable<String> getClassList(@Field("page") String str, @Field("pagesize") String str2, @Field("userid") String str3);

    @FormUrlEncoded
    @POST("getClassXiaoXi.action")
    Observable<String> getClassXiaoXi(@Field("userid") String str, @Field("page") String str2, @Field("pagesize") String str3);

    @FormUrlEncoded
    @POST("xjObtainClassDetailInfo.action")
    Observable<String> getClassXiaoXiById(@Field("classid") String str);

    @FormUrlEncoded
    @POST("getZuList.action")
    Observable<String> getClassZuList(@Field("classid") String str);

    @FormUrlEncoded
    @POST("getDongTai.action")
    Observable<String> getDongTai(@Field("userid") String str, @Field("page") String str2, @Field("pagesize") String str3, @Field("dailytypeid") String str4, @Field("username") String str5);

    @FormUrlEncoded
    @POST("getGerenShuoShuo.action")
    Observable<String> getGerenShuoShuo(@Field("dailyid") String str, @Field("userid") String str2);

    @FormUrlEncoded
    @POST("getHuiFuSubmit.action")
    Observable<String> getHuiFuSubmit(@Field("page") String str, @Field("pagesize") String str2, @Field("classid") String str3, @Field("userid") String str4, @Field("biaoji") String str5);

    @FormUrlEncoded
    @POST("getMainUnreadMessage.action")
    Observable<String> getMainUnreadMessage(@Field("userid") String str);

    @FormUrlEncoded
    @POST("getMyClass.action")
    Observable<String> getMyClass(@Field("page") String str, @Field("pagesize") String str2, @Field("userid") String str3);

    @FormUrlEncoded
    @POST("findPersonInfo.action")
    Observable<String> getMyXinXi(@Field("tsuser.id") String str);

    @FormUrlEncoded
    @POST("getMyZuoYeList.action")
    Observable<String> getMyZuoYeList(@Field("page") String str, @Field("pagesize") String str2, @Field("userid") String str3, @Field("hwtype") String str4, @Field("classid") String str5, @Field("version") String str6);

    @FormUrlEncoded
    @POST("getChildLists.action")
    Observable<String> getNewStudentList(@Field("classid") String str);

    @FormUrlEncoded
    @POST("getChildList.action")
    Observable<String> getStudentList(@Field("classid") String str);

    @FormUrlEncoded
    @POST("getTeacherClass.action")
    Observable<String> getTeacherClass(@Field("page") String str, @Field("pagesize") String str2, @Field("userid") String str3);

    @FormUrlEncoded
    @POST("getTeacherList.action")
    Observable<String> getTeacherList(@Field("page") String str, @Field("pagesize") String str2, @Field("classid") String str3);

    @FormUrlEncoded
    @POST("getUnreadMessage.action")
    Observable<String> getUnreadMessage(@Field("userid") String str);

    @FormUrlEncoded
    @POST("getUnreadStuList.action")
    Observable<String> getUnreadStuList(@Field("itemid") String str);

    @POST("getVersion.action")
    Observable<String> getVersion();

    @FormUrlEncoded
    @POST("loginyanzheng.action")
    Observable<String> getYanZheng(@Field("username") String str);

    @FormUrlEncoded
    @POST("getZuoYeHuiFu.action")
    Observable<String> getZuoYeHuiFu(@Field("page") String str, @Field("pagesize") String str2, @Field("itemid") String str3);

    @FormUrlEncoded
    @POST("getZuoYeList.action")
    Observable<String> getZuoYeList(@Field("itemid") String str, @Field("page") String str2, @Field("pagesize") String str3);

    @POST("queryVideoTypeList.action")
    Observable<String> getweimain();

    @FormUrlEncoded
    @POST("joinClass.action")
    Observable<String> joinClass(@Field("apply.username") String str, @Field("apply.remark") String str2, @Field("apply.classid") String str3, @Field("apply.state") String str4, @Field("apply.role") String str5, @Field("apply.userid") String str6, @Field("apply.xueke") String str7, @Field("apply.bianhao") String str8);

    @FormUrlEncoded
    @POST("login.action")
    Observable<String> login(@Field("username") String str, @Field("password") String str2);

    @FormUrlEncoded
    @POST("OutClass.action")
    Observable<String> outClass(@Field("userid") String str, @Field("classid") String str2);

    @FormUrlEncoded
    @POST("PhoneReceiveServlet")
    Observable<String> phoneReceiveServlet(@Field("uuid") String str);

    @FormUrlEncoded
    @POST("praiseCancel.action")
    Observable<String> praiseCancle(@Field("questionid") String str, @Field("userid") String str2, @Field("itemtype") String str3);

    @FormUrlEncoded
    @POST("isPraise.action")
    Observable<String> praiseIsOrNo(@Field("questionid") String str, @Field("userid") String str2, @Field("itemtype") String str3);

    @FormUrlEncoded
    @POST("praiseSave.action")
    Observable<String> praiseSave(@Field("questionid") String str, @Field("userid") String str2, @Field("itemtype") String str3);

    @FormUrlEncoded
    @POST("register.action")
    Observable<String> registe(@Field("tsuser.username") String str, @Field("tsuser.nickname") String str2, @Field("tsuser.password") String str3, @Field("tsuser.usertype") String str4);

    @FormUrlEncoded
    @POST("editFilePassWord.action")
    Observable<String> resetPassWord(@Field("tableName") String str, @Field("fileName") String str2, @Field("fileValue") String str3, @Field("telephone") String str4);

    @FormUrlEncoded
    @POST("saveAtt.action")
    Observable<String> saveAtts(@Field("attent.filename") String str, @Field("attent.itemid") String str2, @Field("attent.itemtype") String str3, @Field("attent.userid") String str4);

    @FormUrlEncoded
    @POST("saveCLassLiuYan.action")
    Observable<String> saveCLassLiuYan(@Field("huifu.userid") String str, @Field("huifu.itemid") String str2, @Field("huifu.recieveid") String str3, @Field("huifu.content") String str4, @Field("huifu.itemtype") String str5, @Field("huifu.atttype") String str6);

    @FormUrlEncoded
    @POST("pinglunsave.action")
    Observable<String> savePinglun(@Field("dailyid") String str, @Field("replycontent") String str2, @Field("userid") String str3);

    @FormUrlEncoded
    @POST("searchSchoolClassbyNumber.action")
    Observable<String> searchClassXinXi(@Field("classnumber") String str);

    @FormUrlEncoded
    @POST("searchVideoListTop4.action")
    Observable<String> searchVideoListTop4(@Field("Userid") String str);

    @FormUrlEncoded
    @POST("albumMessQuery.action")
    Observable<String> searchWeikeListSecond(@Field("albumid") String str, @Field("userid") String str2);

    @FormUrlEncoded
    @POST("selectChild.action")
    Observable<String> selectChild(@Field("userid") String str, @Field("page") String str2, @Field("pagesize") String str3);

    @FormUrlEncoded
    @POST("SelectMyRole.action")
    Observable<String> selectMyRole(@Field("userid") String str, @Field("classid") String str2);

    @FormUrlEncoded
    @POST("sendNotice.action")
    Observable<String> sendNotice(@Field("senduserid") String str, @Field("receiveid") String str2, @Field("content") String str3, @Field("itemid") String str4, @Field("xuyaoid") String str5, @Field("zhurenid") String str6);

    @FormUrlEncoded
    @POST("updateApply.action")
    Observable<String> updateApply(@Field("applyid") String str, @Field("state") String str2, @Field("classid") String str3, @Field("userid") String str4, @Field("role") String str5, @Field("xueke") String str6, @Field("membername") String str7, @Field("beizhu") String str8, @Field("classname") String str9, @Field("classrole") String str10);

    @FormUrlEncoded
    @POST("queryVersion.action")
    Observable<String> updateContent(@Field("sys_type") String str);

    @FormUrlEncoded
    @POST("updateUnreadMessage.action")
    Observable<String> updateUnreadMessage(@Field("userid") String str, @Field("rtype") String str2, @Field("itemid") String str3);

    @FormUrlEncoded
    @POST("updateUnreadMessageGet.action")
    Observable<String> updateUnreadMessageGet(@Field("userid") String str, @Field("rtype") String str2);

    @FormUrlEncoded
    @POST("updateUnreadZuoYe.action")
    Observable<String> updateUnreadZuoYe(@Field("userid") String str, @Field("itemid") String str2);

    @FormUrlEncoded
    @POST("editFile.action")
    Observable<String> updateXinxi(@Field("tableName") String str, @Field("fileName") String str2, @Field("fileValue") String str3, @Field("tsuser.id") String str4);

    @POST("uploadImage.action")
    @Multipart
    Observable<String> uploadImage(@Part("filename") String str, @Part MultipartBody.Part part);

    @FormUrlEncoded
    @POST("uploadLog.action")
    Observable<String> uploadLog(@Part("filename") String str, @Part MultipartBody.Part part);

    @FormUrlEncoded
    @POST("xjDeleteStudent.action")
    Observable<String> xjDeleteStudent(@Field("classid") String str, @Field("userid") String str2);

    @FormUrlEncoded
    @POST("xjModifyClassInfo.action")
    Observable<String> xjModifyClassInfo(@Field("classid") String str, @Field("classimg") String str2, @Field("classgrade") String str3, @Field("classname") String str4, @Field("schoolname") String str5);

    @FormUrlEncoded
    @POST("queryCommentByVideoid.action")
    Observable<String> xjObtainVedioComment(@Field("videoid") String str, @Field("page") String str2, @Field("pagesize") String str3);

    @FormUrlEncoded
    @POST("videoPlayed.action")
    Observable<String> xjVCheckNum(@Field("videoid") String str);

    @FormUrlEncoded
    @POST("videoRated.action")
    Observable<String> zanzan(@Field("videoid") String str, @Field("userid") String str2, @Field("type") String str3);
}
