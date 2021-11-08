package com.julongtech.user.dao.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * This class corresponds to the database table system_user
 */
public class SystemUserInfo implements Serializable{
    /**
	 * @author julong
	 * @date 2021年10月31日 下午11:09:09
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Database Column Remarks:
     *   用户登录名
     *
     * This field corresponds to the database column system_user.user_id
     *
     * @author julong
     */
    private String userId;

    /**
     * Database Column Remarks:
     *   组织机构编码
     *
     * This field corresponds to the database column system_user.org_id
     *
     * @author julong
     */
    private String orgId;

    /**
     * Database Column Remarks:
     *   用户登录密码
     *
     * This field corresponds to the database column system_user.user_password
     *
     * @author julong
     */
    private String userPassword;

    /**
     * Database Column Remarks:
     *   用户姓名
     *
     * This field corresponds to the database column system_user.user_name
     *
     * @author julong
     */
    private String userName;

    /**
     * Database Column Remarks:
     *   用户年龄：1-999
     *
     * This field corresponds to the database column system_user.user_age
     *
     * @author julong
     */
    private String userAge;

    /**
     * Database Column Remarks:
     *   用户性别：0：男 1：女2：未知
     *
     * This field corresponds to the database column system_user.user_sex
     *
     * @author julong
     */
    private String userSex;

    /**
     * Database Column Remarks:
     *   用户身份证号码
     *
     * This field corresponds to the database column system_user.user_identity
     *
     * @author julong
     */
    private String userIdentity;

    /**
     * Database Column Remarks:
     *   用户地址
     *
     * This field corresponds to the database column system_user.user_address
     *
     * @author julong
     */
    private String userAddress;

    /**
     * Database Column Remarks:
     *   用户电话
     *
     * This field corresponds to the database column system_user.user_phone
     *
     * @author julong
     */
    private String userPhone;

    /**
     * Database Column Remarks:
     *   用户邮箱
     *
     * This field corresponds to the database column system_user.user_mail
     *
     * @author julong
     */
    private String userMail;

    /**
     * Database Column Remarks:
     *   用户描述
     *
     * This field corresponds to the database column system_user.user_desc
     *
     * @author julong
     */
    private String userDesc;

    /**
     * Database Column Remarks:
     *   用户图像url
     *
     * This field corresponds to the database column system_user.user_image
     *
     * @author julong
     */
    private String userImage;

    /**
     * Database Column Remarks:
     *   用户创建时间
     *
     * This field corresponds to the database column system_user.user_create_time
     *
     * @author julong
     */
    private Date userCreateTime;

    /**
     * Database Column Remarks:
     *   用户创建人
     *
     * This field corresponds to the database column system_user.user_create_user_id
     *
     * @author julong
     */
    private String userCreateUserId;

    /**
     * Database Column Remarks:
     *   用户修改时间
     *
     * This field corresponds to the database column system_user.user_update_time
     *
     * @author julong
     */
    private Date userUpdateTime;

    /**
     * Database Column Remarks:
     *   用户修改人
     *
     * This field corresponds to the database column system_user.user_update_user_id
     *
     * @author julong
     */
    private String userUpdateUserId;

    /**
     * Database Column Remarks:
     *   用户密码修改时间
     *
     * This field corresponds to the database column system_user.user_update_password_time
     *
     * @author julong
     */
    private Date userUpdatePasswordTime;

    /**
     * Database Column Remarks:
     *   用户状态：0：正常1：停用
     *
     * This field corresponds to the database column system_user.user_status
     *
     * @author julong
     */
    private String userStatus;

    /**
     * Database Column Remarks:
     *   用户皮肤
     *
     * This field corresponds to the database column system_user.user_skin
     *
     * @author julong
     */
    private String userSkin;

    /**
     * Database Column Remarks:
     *   用户等级
     *
     * This field corresponds to the database column system_user.user_level
     *
     * @author julong
     */
    private String userLevel;

    /**
     * Database Column Remarks:
     *   备用字段1
     *
     * This field corresponds to the database column system_user.user_reserve_a
     *
     * @author julong
     */
    private String userReserveA;

    /**
     * Database Column Remarks:
     *   备用字段2
     *
     * This field corresponds to the database column system_user.user_reserve_b
     *
     * @author julong
     */
    private String userReserveB;

    /**
     * Database Column Remarks:
     *   备用字段3
     *
     * This field corresponds to the database column system_user.user_reserve_c
     *
     * @author julong
     */
    private String userReserveC;

    /**
     * Database Column Remarks:
     *   用户登录名
     * This method returns the value of the database column system_user.user_id
     *
     * @return the value of system_user.user_id
     *
     * @author julong
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Database Column Remarks:
     *   用户登录名
     * This method sets the value of the database column system_user.user_id
     *
     * @param userId the value for system_user.user_id
     *
     * @author julong
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * Database Column Remarks:
     *   组织机构编码
     * This method returns the value of the database column system_user.org_id
     *
     * @return the value of system_user.org_id
     *
     * @author julong
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Database Column Remarks:
     *   组织机构编码
     * This method sets the value of the database column system_user.org_id
     *
     * @param orgId the value for system_user.org_id
     *
     * @author julong
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    /**
     * Database Column Remarks:
     *   用户登录密码
     * This method returns the value of the database column system_user.user_password
     *
     * @return the value of system_user.user_password
     *
     * @author julong
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Database Column Remarks:
     *   用户登录密码
     * This method sets the value of the database column system_user.user_password
     *
     * @param userPassword the value for system_user.user_password
     *
     * @author julong
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }

    /**
     * Database Column Remarks:
     *   用户姓名
     * This method returns the value of the database column system_user.user_name
     *
     * @return the value of system_user.user_name
     *
     * @author julong
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Database Column Remarks:
     *   用户姓名
     * This method sets the value of the database column system_user.user_name
     *
     * @param userName the value for system_user.user_name
     *
     * @author julong
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * Database Column Remarks:
     *   用户年龄：1-999
     * This method returns the value of the database column system_user.user_age
     *
     * @return the value of system_user.user_age
     *
     * @author julong
     */
    public String getUserAge() {
        return userAge;
    }

    /**
     * Database Column Remarks:
     *   用户年龄：1-999
     * This method sets the value of the database column system_user.user_age
     *
     * @param userAge the value for system_user.user_age
     *
     * @author julong
     */
    public void setUserAge(String userAge) {
        this.userAge = userAge == null ? null : userAge.trim();
    }

    /**
     * Database Column Remarks:
     *   用户性别：0：男 1：女2：未知
     * This method returns the value of the database column system_user.user_sex
     *
     * @return the value of system_user.user_sex
     *
     * @author julong
     */
    public String getUserSex() {
        return userSex;
    }

    /**
     * Database Column Remarks:
     *   用户性别：0：男 1：女2：未知
     * This method sets the value of the database column system_user.user_sex
     *
     * @param userSex the value for system_user.user_sex
     *
     * @author julong
     */
    public void setUserSex(String userSex) {
        this.userSex = userSex == null ? null : userSex.trim();
    }

    /**
     * Database Column Remarks:
     *   用户身份证号码
     * This method returns the value of the database column system_user.user_identity
     *
     * @return the value of system_user.user_identity
     *
     * @author julong
     */
    public String getUserIdentity() {
        return userIdentity;
    }

    /**
     * Database Column Remarks:
     *   用户身份证号码
     * This method sets the value of the database column system_user.user_identity
     *
     * @param userIdentity the value for system_user.user_identity
     *
     * @author julong
     */
    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity == null ? null : userIdentity.trim();
    }

    /**
     * Database Column Remarks:
     *   用户地址
     * This method returns the value of the database column system_user.user_address
     *
     * @return the value of system_user.user_address
     *
     * @author julong
     */
    public String getUserAddress() {
        return userAddress;
    }

    /**
     * Database Column Remarks:
     *   用户地址
     * This method sets the value of the database column system_user.user_address
     *
     * @param userAddress the value for system_user.user_address
     *
     * @author julong
     */
    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress == null ? null : userAddress.trim();
    }

    /**
     * Database Column Remarks:
     *   用户电话
     * This method returns the value of the database column system_user.user_phone
     *
     * @return the value of system_user.user_phone
     *
     * @author julong
     */
    public String getUserPhone() {
        return userPhone;
    }

    /**
     * Database Column Remarks:
     *   用户电话
     * This method sets the value of the database column system_user.user_phone
     *
     * @param userPhone the value for system_user.user_phone
     *
     * @author julong
     */
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone == null ? null : userPhone.trim();
    }

    /**
     * Database Column Remarks:
     *   用户邮箱
     * This method returns the value of the database column system_user.user_mail
     *
     * @return the value of system_user.user_mail
     *
     * @author julong
     */
    public String getUserMail() {
        return userMail;
    }

    /**
     * Database Column Remarks:
     *   用户邮箱
     * This method sets the value of the database column system_user.user_mail
     *
     * @param userMail the value for system_user.user_mail
     *
     * @author julong
     */
    public void setUserMail(String userMail) {
        this.userMail = userMail == null ? null : userMail.trim();
    }

    /**
     * Database Column Remarks:
     *   用户描述
     * This method returns the value of the database column system_user.user_desc
     *
     * @return the value of system_user.user_desc
     *
     * @author julong
     */
    public String getUserDesc() {
        return userDesc;
    }

    /**
     * Database Column Remarks:
     *   用户描述
     * This method sets the value of the database column system_user.user_desc
     *
     * @param userDesc the value for system_user.user_desc
     *
     * @author julong
     */
    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc == null ? null : userDesc.trim();
    }

    /**
     * Database Column Remarks:
     *   用户图像url
     * This method returns the value of the database column system_user.user_image
     *
     * @return the value of system_user.user_image
     *
     * @author julong
     */
    public String getUserImage() {
        return userImage;
    }

    /**
     * Database Column Remarks:
     *   用户图像url
     * This method sets the value of the database column system_user.user_image
     *
     * @param userImage the value for system_user.user_image
     *
     * @author julong
     */
    public void setUserImage(String userImage) {
        this.userImage = userImage == null ? null : userImage.trim();
    }

    /**
     * Database Column Remarks:
     *   用户创建时间
     * This method returns the value of the database column system_user.user_create_time
     *
     * @return the value of system_user.user_create_time
     *
     * @author julong
     */
    public Date getUserCreateTime() {
        return userCreateTime;
    }

    /**
     * Database Column Remarks:
     *   用户创建时间
     * This method sets the value of the database column system_user.user_create_time
     *
     * @param userCreateTime the value for system_user.user_create_time
     *
     * @author julong
     */
    public void setUserCreateTime(Date userCreateTime) {
        this.userCreateTime = userCreateTime;
    }

    /**
     * Database Column Remarks:
     *   用户创建人
     * This method returns the value of the database column system_user.user_create_user_id
     *
     * @return the value of system_user.user_create_user_id
     *
     * @author julong
     */
    public String getUserCreateUserId() {
        return userCreateUserId;
    }

    /**
     * Database Column Remarks:
     *   用户创建人
     * This method sets the value of the database column system_user.user_create_user_id
     *
     * @param userCreateUserId the value for system_user.user_create_user_id
     *
     * @author julong
     */
    public void setUserCreateUserId(String userCreateUserId) {
        this.userCreateUserId = userCreateUserId == null ? null : userCreateUserId.trim();
    }

    /**
     * Database Column Remarks:
     *   用户修改时间
     * This method returns the value of the database column system_user.user_update_time
     *
     * @return the value of system_user.user_update_time
     *
     * @author julong
     */
    public Date getUserUpdateTime() {
        return userUpdateTime;
    }

    /**
     * Database Column Remarks:
     *   用户修改时间
     * This method sets the value of the database column system_user.user_update_time
     *
     * @param userUpdateTime the value for system_user.user_update_time
     *
     * @author julong
     */
    public void setUserUpdateTime(Date userUpdateTime) {
        this.userUpdateTime = userUpdateTime;
    }

    /**
     * Database Column Remarks:
     *   用户修改人
     * This method returns the value of the database column system_user.user_update_user_id
     *
     * @return the value of system_user.user_update_user_id
     *
     * @author julong
     */
    public String getUserUpdateUserId() {
        return userUpdateUserId;
    }

    /**
     * Database Column Remarks:
     *   用户修改人
     * This method sets the value of the database column system_user.user_update_user_id
     *
     * @param userUpdateUserId the value for system_user.user_update_user_id
     *
     * @author julong
     */
    public void setUserUpdateUserId(String userUpdateUserId) {
        this.userUpdateUserId = userUpdateUserId == null ? null : userUpdateUserId.trim();
    }

    /**
     * Database Column Remarks:
     *   用户密码修改时间
     * This method returns the value of the database column system_user.user_update_password_time
     *
     * @return the value of system_user.user_update_password_time
     *
     * @author julong
     */
    public Date getUserUpdatePasswordTime() {
        return userUpdatePasswordTime;
    }

    /**
     * Database Column Remarks:
     *   用户密码修改时间
     * This method sets the value of the database column system_user.user_update_password_time
     *
     * @param userUpdatePasswordTime the value for system_user.user_update_password_time
     *
     * @author julong
     */
    public void setUserUpdatePasswordTime(Date userUpdatePasswordTime) {
        this.userUpdatePasswordTime = userUpdatePasswordTime;
    }

    /**
     * Database Column Remarks:
     *   用户状态：0：正常1：停用
     * This method returns the value of the database column system_user.user_status
     *
     * @return the value of system_user.user_status
     *
     * @author julong
     */
    public String getUserStatus() {
        return userStatus;
    }

    /**
     * Database Column Remarks:
     *   用户状态：0：正常1：停用
     * This method sets the value of the database column system_user.user_status
     *
     * @param userStatus the value for system_user.user_status
     *
     * @author julong
     */
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus == null ? null : userStatus.trim();
    }

    /**
     * Database Column Remarks:
     *   用户皮肤
     * This method returns the value of the database column system_user.user_skin
     *
     * @return the value of system_user.user_skin
     *
     * @author julong
     */
    public String getUserSkin() {
        return userSkin;
    }

    /**
     * Database Column Remarks:
     *   用户皮肤
     * This method sets the value of the database column system_user.user_skin
     *
     * @param userSkin the value for system_user.user_skin
     *
     * @author julong
     */
    public void setUserSkin(String userSkin) {
        this.userSkin = userSkin == null ? null : userSkin.trim();
    }

    /**
     * Database Column Remarks:
     *   用户等级
     * This method returns the value of the database column system_user.user_level
     *
     * @return the value of system_user.user_level
     *
     * @author julong
     */
    public String getUserLevel() {
        return userLevel;
    }

    /**
     * Database Column Remarks:
     *   用户等级
     * This method sets the value of the database column system_user.user_level
     *
     * @param userLevel the value for system_user.user_level
     *
     * @author julong
     */
    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel == null ? null : userLevel.trim();
    }

    /**
     * Database Column Remarks:
     *   备用字段1
     * This method returns the value of the database column system_user.user_reserve_a
     *
     * @return the value of system_user.user_reserve_a
     *
     * @author julong
     */
    public String getUserReserveA() {
        return userReserveA;
    }

    /**
     * Database Column Remarks:
     *   备用字段1
     * This method sets the value of the database column system_user.user_reserve_a
     *
     * @param userReserveA the value for system_user.user_reserve_a
     *
     * @author julong
     */
    public void setUserReserveA(String userReserveA) {
        this.userReserveA = userReserveA == null ? null : userReserveA.trim();
    }

    /**
     * Database Column Remarks:
     *   备用字段2
     * This method returns the value of the database column system_user.user_reserve_b
     *
     * @return the value of system_user.user_reserve_b
     *
     * @author julong
     */
    public String getUserReserveB() {
        return userReserveB;
    }

    /**
     * Database Column Remarks:
     *   备用字段2
     * This method sets the value of the database column system_user.user_reserve_b
     *
     * @param userReserveB the value for system_user.user_reserve_b
     *
     * @author julong
     */
    public void setUserReserveB(String userReserveB) {
        this.userReserveB = userReserveB == null ? null : userReserveB.trim();
    }

    /**
     * Database Column Remarks:
     *   备用字段3
     * This method returns the value of the database column system_user.user_reserve_c
     *
     * @return the value of system_user.user_reserve_c
     *
     * @author julong
     */
    public String getUserReserveC() {
        return userReserveC;
    }

    /**
     * Database Column Remarks:
     *   备用字段3
     * This method sets the value of the database column system_user.user_reserve_c
     *
     * @param userReserveC the value for system_user.user_reserve_c
     *
     * @author julong
     */
    public void setUserReserveC(String userReserveC) {
        this.userReserveC = userReserveC == null ? null : userReserveC.trim();
    }

	@Override
	public String toString() {
		return "SystemUserInfo [userId=" + userId + ", orgId=" + orgId + ", userPassword=" + userPassword
				+ ", userName=" + userName + ", userAge=" + userAge + ", userSex=" + userSex + ", userIdentity="
				+ userIdentity + ", userAddress=" + userAddress + ", userPhone=" + userPhone + ", userMail=" + userMail
				+ ", userDesc=" + userDesc + ", userImage=" + userImage + ", userCreateTime=" + userCreateTime
				+ ", userCreateUserId=" + userCreateUserId + ", userUpdateTime=" + userUpdateTime
				+ ", userUpdateUserId=" + userUpdateUserId + ", userUpdatePasswordTime=" + userUpdatePasswordTime
				+ ", userStatus=" + userStatus + ", userSkin=" + userSkin + ", userLevel=" + userLevel
				+ ", userReserveA=" + userReserveA + ", userReserveB=" + userReserveB + ", userReserveC=" + userReserveC
				+ "]";
	}
    
    
}