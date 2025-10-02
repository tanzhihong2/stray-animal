<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <img src="@/assets/front-logo.jpg" class="logo" alt="宠物领养救助系统">
          <h2 class="title">宠物领养救助系统</h2>
        </div>
        
        <el-form ref="userForm" :model="user" :rules="rules">
          <el-form-item prop="username">
            <el-input
              size="large"
              prefix-icon="iconfont icon-r-user1"
              v-model="user.username"
              placeholder="请输入用户名"
              @keyup.enter.native="login"
            >
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              size="large"
              prefix-icon="iconfont icon-r-lock"
              show-password
              v-model="user.password"
              placeholder="请输入密码"
              @keyup.enter.native="login"
            >
            </el-input>
          </el-form-item>
          
          <div class="options">
            <el-checkbox v-model="rememberMe">记住账号</el-checkbox>
            <a href="javascript:;" @click="handlePass">忘记密码？</a>
          </div>
          
          <el-button
            type="primary"
            @click="login"
            class="login-btn"
            :loading="loading"
          >登录</el-button>
          
          <div class="register-link">
            <span>还没有账号？</span>
            <el-button type="text" @click="$router.push('/register')">立即注册</el-button>
          </div>
        </el-form>
      </div>
      
      <div class="login-footer">
        <p>© 2025 流浪动物救助系统 版权所有</p>
      </div>
    </div>
    
    <!-- 忘记密码对话框 -->
    <el-dialog title="重置密码" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="passForm" :model="pass" :rules="passRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="pass.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pass.newPassword" autocomplete="off" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="passwordBack">重置</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { resetRouter, setRoutes } from "@/router";

export default {
  name: "Login",
  data() {
    return {
      user: {},
      pass: {},
      dialogFormVisible: false,
      rememberMe: false,
      loading: false,
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在3到10个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
        ]
      },
      passRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在3到10个字符', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // 重置路由
    resetRouter()
    
    // 如果之前记住过用户名
    const savedUser = localStorage.getItem('rememberedUser')
    if (savedUser) {
      this.user = JSON.parse(savedUser)
      this.rememberMe = true
    }
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.loading = true
          this.request.post("/user/login", this.user).then(res => {
            if (res.code === '200') {
              // 如果选择记住我
              if (this.rememberMe) {
                localStorage.setItem('rememberedUser', JSON.stringify(this.user));
              } else {
                localStorage.removeItem('rememberedUser');
              }
              
              // 存储用户信息到浏览器
              localStorage.setItem("user", JSON.stringify(res.data))
              localStorage.setItem("menus", JSON.stringify(res.data.menus))
              
              // 动态设置当前用户的路由
              setRoutes()
              
              this.$message.success("登录成功")
              this.$router.push("/front/home")
            } else {
              this.$message.error(res.msg)
            }
          }).catch(e => {
            console.log(e);
            if (e.response == undefined || e.response.data == undefined) {
              this.$message({
                showClose: true,
                message: e,
                type: "error",
                duration: 5000
              });
            } else {
              this.$message({
                showClose: true,
                message: e.response.data,
                type: "error",
                duration: 5000
              });
            }
          }).finally(() => {
            this.loading = false
          })
        }
      });
    },
    handlePass() {
      this.dialogFormVisible = true
      this.pass = {}
    },
    passwordBack() {
      this.$refs.passForm.validate((valid) => {
        if (valid) {
          this.request.put("/user/reset", this.pass).then(res => {
            if (res.code === '200') {
              this.$message.success("重置密码成功，新密码为：123，请尽快修改密码")
              this.dialogFormVisible = false
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url("../assets/background.jpg") center top / cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: fadeIn 1s ease;
}

.login-header {
  text-align: center;
  padding: 30px 20px 20px;
}

.logo {
  width: 50px;
  height: 50px;
  vertical-align: middle;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: rotate(10deg);
}

.title {
  margin: 20px 0 30px;
  font-size: 24px;
  color: #8b6f47;
  font-weight: 600;
}

.login-card {
  padding: 20px 40px 40px;
}

.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0 20px;
}

.login-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  border-radius: 30px;
  background-color: #f0c040;
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background-color: #dca62a;
  transform: translateY(-2px);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link .el-button--text {
  color: #8b6f47;
  font-weight: 500;
}

.login-footer {
  text-align: center;
  padding: 20px;
  font-size: 14px;
  color: #999;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
