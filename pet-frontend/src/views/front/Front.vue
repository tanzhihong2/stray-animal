<template>
  <div class="front-container">
<!--    头部-->
    <div class="header-section">
      <div class="header-content">
        <div class="logo-section">
          <img src="../../assets/front-logo.jpg" alt="" class="logo-img">
          <div class="logo-text">流浪动物救助系统</div>
        </div>
        <div class="nav-section">
          <el-menu :default-active="'1'" class="el-menu-demo" mode="horizontal" router>
            <el-menu-item index="/front/home">首页</el-menu-item>
            <el-menu-item index="/front/adopt">宠物领养</el-menu-item>
            <el-menu-item index="/front/salvation">宠物救助</el-menu-item>
            <el-menu-item index="/front/lost">丢失宠物查看</el-menu-item>
            <el-menu-item index="/front/article">宠物论坛</el-menu-item>
          </el-menu>
        </div>
        <div class="user-section">
          <div v-if="!user.username" class="auth-buttons">
            <el-button type="primary" @click="$router.push('/login')" class="auth-btn">登录</el-button>
            <el-button @click="$router.push('/register')" class="auth-btn">注册</el-button>
          </div>
          <div v-else class="user-info">
            <el-dropdown class="user-dropdown" trigger="click">
              <div class="user-profile">
                <img :src="$store.state.baseApi+user.avatarUrl" alt="" class="user-avatar">
                <span class="user-name">{{ user.nickname }}</span>
                <i class="el-icon-arrow-down user-arrow"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="user-menu">
                <el-dropdown-item v-if="user.role === 'ROLE_ADMIN'">
                  <router-link to="/home" class="menu-link">后台管理</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/front/password" class="menu-link">修改密码</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/front/person" class="menu-link">个人信息</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <span class="menu-link logout-link" @click="logout">退出</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: "Front",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {

  },
  methods: {
    logout() {
      this.$store.commit("logout")
      this.$message.success("退出成功")
    }
  }
}
</script>

<style scoped>
.front-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 头部样式 */
.header-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  height: 70px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
}

.logo-section {
  display: flex;
  align-items: center;
  min-width: 280px;
}

.logo-img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  margin-right: 15px;
  transition: transform 0.3s ease;
}

.logo-img:hover {
  transform: rotate(10deg);
}

.logo-text {
  font-size: 22px;
  color: #2c3e50;
  font-weight: 700;
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-section .el-menu {
  border-bottom: none;
  background: transparent;
}

.nav-section .el-menu-item {
  height: 70px;
  line-height: 70px;
  margin: 0 10px;
  border-radius: 35px;
  font-size: 16px;
  font-weight: 500;
  color: #5a6c7d;
  transition: all 0.3s ease;
  border-bottom: none !important;
}

.nav-section .el-menu-item:hover {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  transform: translateY(-2px);
}

.nav-section .el-menu-item.is-active {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.user-section {
  min-width: 200px;
  display: flex;
  justify-content: flex-end;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.auth-btn {
  border-radius: 25px;
  padding: 10px 25px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.auth-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.user-dropdown {
  cursor: pointer;
}

.user-profile {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  border-radius: 25px;
  background: rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
}

.user-profile:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  border: 2px solid #667eea;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-right: 8px;
}

.user-arrow {
  color: #667eea;
  font-size: 14px;
}

.user-menu {
  border-radius: 10px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  border: none;
  padding: 10px 0;
}

.menu-link {
  color: #5a6c7d;
  text-decoration: none;
  display: block;
  padding: 8px 20px;
  font-weight: 500;
  transition: color 0.3s ease;
}

.menu-link:hover {
  color: #667eea;
}

.logout-link {
  cursor: pointer;
}

.logout-link:hover {
  color: #e74c3c;
}

/* 主要内容区域 */
.main-content {
  width: 100%;
  min-height: calc(100vh - 70px);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    padding: 0 20px;
  }
  
  .main-content {
    width: 95%;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 15px 20px;
  }
  
  .logo-section {
    margin-bottom: 15px;
    min-width: auto;
  }
  
  .nav-section {
    margin-bottom: 15px;
  }
  
  .nav-section .el-menu {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .nav-section .el-menu-item {
    height: 40px;
    line-height: 40px;
    margin: 5px;
    font-size: 14px;
  }
  
  .user-section {
    min-width: auto;
  }
  
  .main-content {
    width: 100%;
    padding: 0 10px;
  }
}
</style>
