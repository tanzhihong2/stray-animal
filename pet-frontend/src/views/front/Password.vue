<template>
  <div class="password-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>
    
    <!-- 居中卡片容器 -->
    <div class="center-container">
      <div class="password-card">
        <!-- 卡片头部 -->
        <div class="card-header">
          <div class="header-decoration">
            <div class="decoration-line"></div>
            <div class="header-icon">
              <i class="el-icon-lock"></i>
            </div>
            <div class="decoration-line"></div>
          </div>
          <h1 class="card-title">修改密码</h1>
          <p class="card-subtitle">为了保障您的账户安全，请定期修改您的登录密码</p>
        </div>

        <!-- 安全提示 -->
        <div class="security-tips">
          <div class="tip-item">
            <i class="el-icon-shield"></i>
            <span>密码长度不少于6位</span>
          </div>
          <div class="tip-item">
            <i class="el-icon-view"></i>
            <span>建议包含字母和数字</span>
          </div>
          <div class="tip-item">
            <i class="el-icon-refresh"></i>
            <span>定期更换密码更安全</span>
          </div>
        </div>

        <!-- 表单区域 -->
        <div class="form-section">
          <el-form 
            label-width="100px" 
            :model="form" 
            :rules="rules" 
            ref="pass"
            class="password-form"
            label-position="left"
          >
            <div class="form-row">
              <el-form-item label="原密码" prop="password">
                <el-input 
                  v-model="form.password" 
                  autocomplete="off" 
                  show-password
                  prefix-icon="el-icon-lock"
                  placeholder="请输入您的原密码"
                  size="large"
                >
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="新密码" prop="newPassword">
                <el-input 
                  v-model="form.newPassword" 
                  autocomplete="off" 
                  show-password
                  prefix-icon="el-icon-key"
                  placeholder="请输入新密码"
                  size="large"
                >
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="form.confirmPassword" 
                  autocomplete="off" 
                  show-password
                  prefix-icon="el-icon-check"
                  placeholder="请再次输入新密码"
                  size="large"
                  @keyup.enter.native="save"
                >
                </el-input>
              </el-form-item>
            </div>

            <!-- 密码强度显示 -->
            <div class="password-strength" v-if="form.newPassword">
              <div class="strength-label">密码强度：</div>
              <div class="strength-bar">
                <div class="strength-level" :class="passwordStrengthClass"></div>
              </div>
              <div class="strength-text">{{ passwordStrengthText }}</div>
            </div>
          </el-form>
        </div>

        <!-- 按钮区域 -->
        <div class="button-section">
          <el-button 
            type="primary" 
            size="large" 
            @click="save" 
            :loading="saveLoading"
            class="save-button"
          >
            <i class="el-icon-check"></i>
            确认修改
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Password",
  data() {
    // 验证新密码强度
    const validateNewPassword = (rule, value, callback) => {
      if (value && value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }
    
    // 验证确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value && value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      saveLoading: false,
      rules: {
        password: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
          { min: 3, message: '原密码长度不能少于3位', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { validator: validateNewPassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
      }
    }
  },
  computed: {
    // 计算密码强度
    passwordStrength() {
      const password = this.form.newPassword
      if (!password) return 0
      
      let strength = 0
      if (password.length >= 6) strength += 1
      if (password.length >= 8) strength += 1
      if (/[a-z]/.test(password)) strength += 1
      if (/[A-Z]/.test(password)) strength += 1
      if (/\d/.test(password)) strength += 1
      if (/[^\w\s]/.test(password)) strength += 1
      
      return Math.min(strength, 4)
    },
    
    passwordStrengthClass() {
      const strength = this.passwordStrength
      if (strength <= 1) return 'weak'
      if (strength <= 2) return 'medium'
      if (strength <= 3) return 'good'
      return 'strong'
    },
    
    passwordStrengthText() {
      const strength = this.passwordStrength
      if (strength <= 1) return '弱'
      if (strength <= 2) return '中等'
      if (strength <= 3) return '良好'
      return '强'
    }
  },
  created() {
    this.form.username = this.user.username
  },
  methods: {
    save() {
      this.$refs.pass.validate((valid) => {
        if (valid) {
          if (this.form.newPassword !== this.form.confirmPassword) {
            this.$message.error("两次输入的新密码不相同")
            return false
          }
          
          this.saveLoading = true
          this.request.post("/user/password", this.form).then(res => {
            if (res.code === '200') {
              this.$message({
                message: '密码修改成功！请使用新密码重新登录',
                type: 'success',
                duration: 3000
              })
              
              // 清空表单
              this.form = { username: this.user.username }
              this.$refs.pass.resetFields()
              
              // 延迟登出
              setTimeout(() => {
                this.$store.commit("logout")
                this.$router.push('/login')
              }, 2000)
            } else {
              this.$message.error(res.msg || '修改失败')
            }
          }).catch(() => {
            this.$message.error('网络错误，请稍后再试')
          }).finally(() => {
            this.saveLoading = false
          })
        } else {
          this.$message.warning('请正确填写表单信息')
        }
      })
    },
  }
}
</script>

<style scoped>
/* 全局页面容器 */
.password-page {
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
.password-card {
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

.password-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.password-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2);
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 30px;
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

/* 安全提示 */
.security-tips {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.tip-item i {
  color: #667eea;
  font-size: 14px;
}

/* 表单区域 */
.form-section {
  margin-bottom: 30px;
}

.form-row {
  margin-bottom: 20px;
}

.password-form .el-form-item {
  margin-bottom: 0;
}

.password-form .el-form-item__label {
  color: #333;
  font-weight: 500;
  padding-bottom: 8px;
}

.password-form .el-input__inner {
  border-radius: 12px;
  border: 2px solid #e4e7ed;
  padding: 15px 20px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  height: auto;
}

.password-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.password-form .el-input--large .el-input__inner {
  height: 50px;
}

.password-form .el-input__prefix {
  left: 15px;
  color: #667eea;
}

/* 密码强度显示 */
.password-strength {
  margin-top: 15px;
  padding: 15px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.strength-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.strength-bar {
  width: 100%;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 8px;
}

.strength-level {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-level.weak {
  width: 25%;
  background: #ff4757;
}

.strength-level.medium {
  width: 50%;
  background: #ffa502;
}

.strength-level.good {
  width: 75%;
  background: #2ed573;
}

.strength-level.strong {
  width: 100%;
  background: #1e90ff;
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
  text-align: right;
}

.strength-text {
  color: #ff4757;
}

.password-strength .strength-text {
  color: inherit;
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
  .password-page {
    padding: 20px 15px;
  }
  
  .password-card {
    padding: 30px 20px;
    margin: 20px;
    border-radius: 20px;
  }
  
  .card-title {
    font-size: 24px;
  }
  
  .security-tips {
    padding: 15px;
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
  .password-card {
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
  
  .tip-item {
    font-size: 11px;
  }
}
</style>
