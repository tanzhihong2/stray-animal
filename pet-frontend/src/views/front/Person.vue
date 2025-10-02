<template>
  <div class="person-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>
    
    <!-- 居中卡片容器 -->
    <div class="center-container">
      <div class="profile-card">
        <!-- 卡片头部 -->
        <div class="card-header">
          <div class="header-decoration">
            <div class="decoration-line"></div>
            <div class="header-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="decoration-line"></div>
          </div>
          <h1 class="card-title">个人信息</h1>
          <p class="card-subtitle">完善您的个人资料，享受更好的服务体验</p>
        </div>

        <!-- 头像区域 -->
        <div class="avatar-section">
          <div class="avatar-container">
            <el-upload
              class="avatar-uploader"
              :action="$store.state.baseApi+'/file/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
            >
              <div class="avatar-wrapper">
                <img v-if="form.avatarUrl" :src="$store.state.baseApi+form.avatarUrl" class="avatar-img">
                <div v-else class="avatar-placeholder">
                  <i class="el-icon-user"></i>
                </div>
                <div class="avatar-overlay">
                  <i class="el-icon-camera"></i>
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
          </div>
        </div>

        <!-- 表单区域 -->
        <div class="form-section">
          <el-form 
            label-width="80px" 
            class="profile-form"
            label-position="left"
          >
            <div class="form-row">
              <el-form-item label="用户名">
                <el-input 
                  v-model="form.username" 
                  disabled 
                  prefix-icon="el-icon-lock"
                  placeholder="系统分配的用户名"
                >
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="姓名">
                <el-input 
                  v-model="form.nickname" 
                  prefix-icon="el-icon-user"
                  placeholder="请输入您的姓名"
                >
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="性别">
                <el-radio-group v-model="form.sex" class="gender-group">
                  <el-radio label="男" class="gender-radio">
                    <i class="el-icon-male"></i>
                    男
                  </el-radio>
                  <el-radio label="女" class="gender-radio">
                    <i class="el-icon-female"></i>
                    女
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="生日">
                <el-date-picker 
                  clearable 
                  style="width: 100%" 
                  v-model="form.birth" 
                  type="date" 
                  value-format="yyyy-MM-dd" 
                  placeholder="选择日期"
                  prefix-icon="el-icon-date"
                >
                </el-date-picker>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="邮箱">
                <el-input 
                  v-model="form.email" 
                  prefix-icon="el-icon-message"
                  placeholder="请输入您的邮箱地址"
                >
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="电话">
                <el-input 
                  v-model="form.phone" 
                  prefix-icon="el-icon-phone"
                  placeholder="请输入您的电话号码"
                >
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="地址">
                <el-input 
                  v-model="form.address" 
                  prefix-icon="el-icon-location"
                  placeholder="请输入您的详细地址"
                >
                </el-input>
              </el-form-item>
            </div>
          </el-form>
        </div>

        <!-- 按钮区域 -->
        <div class="button-section">
          <el-button 
            type="primary" 
            size="large" 
            @click="save" 
            class="save-button"
          >
            <i class="el-icon-check"></i>
            确定
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Person",
  data() {
    return {
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.getUser().then(res => {
      console.log(res)
      this.form = res
    })
  },
  methods: {
    async getUser() {
      return (await this.request.get("/user/username/" + this.user.username)).data
    },
    save() {
      this.request.post("/user", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")

          // 触发父级更新User的方法
          this.$emit("refreshUser")

          // 更新浏览器存储的用户信息
          this.getUser().then(res => {
            res.token = JSON.parse(localStorage.getItem("user")).token
            localStorage.setItem("user", JSON.stringify(res))
          })

        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAvatarSuccess(res) {
      this.form.avatarUrl = res
    }
  }
}
</script>

<style scoped>
/* 全局页面容器 */
.person-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: -75px;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 居中容器 */
.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 80px);
  position: relative;
  z-index: 2;
}

/* 主要卡片 */
.profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 40px;
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.profile-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.profile-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2);
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  gap: 15px;
}

.decoration-line {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
}

.header-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  }
  50% {
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
    transform: scale(1.05);
  }
  100% {
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  }
}

.card-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 10px 0;
}

.card-subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
  line-height: 1.6;
}

/* 头像区域 */
.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.avatar-container {
  display: inline-block;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: white;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
  border-radius: 50%;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay i {
  font-size: 24px;
  margin-bottom: 5px;
}

.avatar-overlay span {
  font-size: 12px;
  font-weight: 500;
}

/* 表单区域 */
.form-section {
  margin-bottom: 30px;
}

.form-row {
  margin-bottom: 20px;
}

.profile-form .el-form-item {
  margin-bottom: 0;
}

.profile-form .el-form-item__label {
  color: #333;
  font-weight: 500;
  padding-bottom: 8px;
}

.profile-form .el-input__inner {
  border-radius: 12px;
  border: 2px solid #e4e7ed;
  padding: 12px 15px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
}

.profile-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.profile-form .el-input__inner:disabled {
  background-color: #f5f7fa;
  color: #999;
}

.profile-form .el-date-editor .el-input__inner {
  padding-left: 40px;
}

/* 性别选择 */
.gender-group {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.gender-radio {
  padding: 12px 24px;
  border: 2px solid #e4e7ed;
  border-radius: 25px;
  transition: all 0.3s ease;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.8);
  margin-right: 0;
}

.gender-radio:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.gender-radio.is-checked {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.gender-radio i {
  margin-right: 5px;
}

/* 按钮区域 */
.button-section {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.save-button {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 25px;
  padding: 15px 50px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.save-button:hover {
  background: linear-gradient(135deg, #5a6fd8, #6a42a0);
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.save-button:active {
  transform: translateY(-1px);
}

.save-button i {
  margin-right: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .person-page {
    padding: 20px 15px;
  }
  
  .profile-card {
    padding: 30px 20px;
    margin: 20px;
    border-radius: 20px;
  }
  
  .card-title {
    font-size: 24px;
  }
  
  .avatar-wrapper {
    width: 100px;
    height: 100px;
  }
  
  .gender-group {
    flex-direction: column;
    gap: 15px;
  }
  
  .save-button {
    padding: 12px 40px;
    font-size: 14px;
  }
  
  .bg-circle {
    display: none;
  }
}

@media (max-width: 480px) {
  .profile-card {
    margin: 10px;
    padding: 25px 15px;
  }
  
  .header-decoration {
    gap: 10px;
  }
  
  .decoration-line {
    width: 40px;
  }
  
  .header-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
}
</style>
