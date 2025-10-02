<template>
  <div class="home-container">
    <!-- 全新轮播图区域 -->
    <div class="hero-banner">
      <div class="banner-container">
        <div class="slide-wrapper">
          <div 
            v-for="(slide, index) in slides" 
            :key="index"
            class="slide" 
            :class="{ active: currentSlide === index }"
            :style="{ backgroundImage: `url(${slide.image})`, backgroundSize: 'cover', backgroundPosition: 'center' }"
          >
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <h1 class="slide-title">{{ slide.title }}</h1>
              <p class="slide-subtitle">{{ slide.subtitle }}</p>
            </div>
          </div>
        </div>
        
        <!-- 导航箭头 -->
        <button class="nav-btn prev" @click="prevSlide">
          <i class="el-icon-arrow-left"></i>
        </button>
        <button class="nav-btn next" @click="nextSlide">
          <i class="el-icon-arrow-right"></i>
        </button>
        
        <!-- 指示器 -->
        <div class="indicators">
          <span 
            v-for="(slide, index) in slides" 
            :key="index"
            class="indicator" 
            :class="{ active: currentSlide === index }"
            @click="goToSlide(index)"
          ></span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 快速导航 -->
      <div class="quick-nav">
        <div class="nav-item" @click="$router.push('/front/adopt')">
          <div class="nav-icon adopt-icon">
            <i class="el-icon-user"></i>
          </div>
          <h3>宠物领养</h3>
          <p>给流浪动物一个温暖的家</p>
        </div>
        <div class="nav-item" @click="$router.push('/front/salvation')">
          <div class="nav-icon rescue-icon">
            <i class="el-icon-first-aid-kit"></i>
          </div>
          <h3>紧急救助</h3>
          <p>及时救助受伤的小生命</p>
        </div>
        <div class="nav-item" @click="$router.push('/front/lost')">
          <div class="nav-icon lost-icon">
            <i class="el-icon-search"></i>
          </div>
          <h3>寻找宠物</h3>
          <p>帮助走失宠物回家</p>
        </div>
        <div class="nav-item" @click="$router.push('/front/article')">
          <div class="nav-icon forum-icon">
            <i class="el-icon-chat-line-round"></i>
          </div>
          <h3>爱心论坛</h3>
          <p>分享救助经验与故事</p>
        </div>
      </div>

      <!-- 重要公告 -->
      <div class="section announcement-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-bell"></i>
            重要公告
          </h2>
          <span class="section-subtitle">了解最新救助动态</span>
        </div>
        <div class="announcement-content">
          <el-card 
            v-for="(notice, index) in notices.slice(0, 3)" 
            :key="index" 
            class="announcement-card"
            shadow="hover"
          >
            <div class="announcement-item">
              <div class="announcement-header">
                <h3 class="announcement-title">{{ notice.name }}</h3>
                <span class="announcement-time">{{ notice.time }}</span>
              </div>
              <p class="announcement-content-text">{{ notice.content }}</p>
              <div class="announcement-footer">
                <el-button type="text" class="read-more" @click="viewNotice(notice)">阅读更多</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 宠物知识 -->
      <div class="section knowledge-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-reading"></i>
            宠物知识
          </h2>
          <span class="section-subtitle">科学养护，健康成长</span>
        </div>
        <div class="knowledge-grid">
          <div 
            v-for="(article, index) in articles.slice(0, 6)" 
            :key="index" 
            class="knowledge-card"
            @click="viewArticle(article.id, article.content)"
          >
            <div class="knowledge-image">
              <img :src="$store.state.baseApi + article.img" :alt="article.name">
            </div>
            <div class="knowledge-content">
              <h3 class="knowledge-title">{{ article.name }}</h3>
              <div class="knowledge-meta">
                <span><i class="el-icon-view"></i>{{ article.read1 || 0 }}</span>
                <span><i class="el-icon-time"></i>{{ article.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门活动 -->
      <div class="section activity-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-star-on"></i>
            热门活动
          </h2>
          <span class="section-subtitle">参与公益，传递爱心</span>
        </div>
        <div class="activity-list">
          <div 
            v-for="(activity, index) in activities.slice(0, 4)" 
            :key="index" 
            class="activity-card"
            @click="viewActivity(activity.content)"
          >
            <div class="activity-image">
              <img :src="$store.state.baseApi + activity.img" :alt="activity.name">
              <div class="activity-date">
                <span class="month">{{ getMonth(activity.time) }}</span>
                <span class="day">{{ getDay(activity.time) }}</span>
              </div>
            </div>
            <div class="activity-info">
              <h3 class="activity-title">{{ activity.name }}</h3>
              <div class="activity-details">
                <p class="activity-location">
                  <i class="el-icon-location"></i>
                  {{ activity.address }}
                </p>
                <p class="activity-participants">
                  <i class="el-icon-user"></i>
                  {{ activity.num }}人参与
                </p>
              </div>
              <div class="activity-footer">
                <el-button type="primary" size="small" class="join-btn">报名参加</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 救助统计 -->
      <div class="section stats-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-data-line"></i>
            救助成果
          </h2>
          <span class="section-subtitle">每一个数字都是一份爱心</span>
        </div>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-number">1,250+</div>
            <div class="stat-label">成功领养</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <i class="el-icon-first-aid-kit"></i>
            </div>
            <div class="stat-number">3,480+</div>
            <div class="stat-label">紧急救助</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <i class="el-icon-coin"></i>
            </div>
            <div class="stat-number">85,600+</div>
            <div class="stat-label">爱心捐赠</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <i class="el-icon-heart"></i>
            </div>
            <div class="stat-number">12,890+</div>
            <div class="stat-label">志愿者</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 公告内容对话框 -->
    <el-dialog title="公告详情" :visible.sync="noticeDialogVisible" width="60%">
      <div class="notice-detail">
        <p class="notice-content">{{ noticeContent }}</p>
      </div>
    </el-dialog>

    <!-- 文章内容对话框 -->
    <el-dialog title="宠物知识详情" :visible.sync="articleDialogVisible" width="60%">
      <mavon-editor
        class="md"
        :value="articleContent"
        :subfield="false"
        :defaultOpen="'preview'"
        :toolbarsFlag="false"
        :editable="false"
        :scrollStyle="true"
        :ishljs="true"
      />
    </el-dialog>

    <!-- 活动内容对话框 -->
    <el-dialog title="活动详情" :visible.sync="activityDialogVisible" width="60%">
      <mavon-editor
        class="md"
        :value="activityContent"
        :subfield="false"
        :defaultOpen="'preview'"
        :toolbarsFlag="false"
        :editable="false"
        :scrollStyle="true"
        :ishljs="true"
      />
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "NewHomePage",
  data() {
    return {
      slides: [
        {
          image: require('@/assets/dog.png'),
          title: '每一个生命都值得被温柔以待',
          subtitle: '用爱心点亮流浪动物的希望之路'
        },
        {
          image: require('@/assets/miao.png'),
          title: '给它们一个温暖的家',
          subtitle: '让每一只流浪的小生命都能感受到家的温暖'
        },
        {
          image: require('@/assets/90.jpg'),
          title: '携手共建爱心世界',
          subtitle: '您的每一份关爱都是它们生命中的阳光'
        }
      ],
      currentSlide: 0,
      autoplayTimer: null,
      notices: [],
      articles: [],
      activities: [],
      articleDialogVisible: false,
      articleContent: '',
      activityDialogVisible: false,
      activityContent: '',
      noticeDialogVisible: false,
      noticeContent: ''
    }
  },
  mounted() {
    this.loadData();
    this.startAutoplay();
  },
  beforeDestroy() {
    this.stopAutoplay();
  },
  methods: {
    async loadData() {
      await Promise.all([
        this.loadNotices(),
        this.loadArticles(),
        this.loadActivities()
      ]);
    },
    async loadNotices() {
      try {
        const res = await this.request.get("/notice/front");
        this.notices = res.data || [];
      } catch (error) {
        console.error('加载公告失败:', error);
      }
    },
    async loadArticles() {
      try {
        const res = await this.request.get("/articleKp/page", {
          params: { pageNum: 1, pageSize: 6 }
        });
        this.articles = res.data.records || [];
      } catch (error) {
        console.error('加载文章失败:', error);
      }
    },
    async loadActivities() {
      try {
        const res = await this.request.get("/activity/page", {
          params: { pageNum: 1, pageSize: 4 }
        });
        this.activities = res.data.records || [];
      } catch (error) {
        console.error('加载活动失败:', error);
      }
    },
    handleAction(type) {
      if (type === 'help') {
        this.$router.push('/front/salvation');
      } else if (type === 'donate') {
        this.$message.info('捐赠功能即将开放，敬请期待！');
      }
    },
    viewArticle(id, content) {
      if (id) {
        this.request.get("/articleKp/" + id);
      }
      this.articleContent = content;
      this.articleDialogVisible = true;
    },
    viewNotice(notice) {
      this.noticeContent = notice.content;
      this.noticeDialogVisible = true;
    },
    viewActivity(content) {
      this.activityContent = content;
      this.activityDialogVisible = true;
    },
    startAutoplay() {
      this.autoplayTimer = setInterval(() => {
        this.nextSlide();
      }, 4000);
    },
    stopAutoplay() {
      if (this.autoplayTimer) {
        clearInterval(this.autoplayTimer);
        this.autoplayTimer = null;
      }
    },
    nextSlide() {
      this.currentSlide = (this.currentSlide + 1) % this.slides.length;
    },
    prevSlide() {
      this.currentSlide = this.currentSlide === 0 ? this.slides.length - 1 : this.currentSlide - 1;
    },
    goToSlide(index) {
      this.currentSlide = index;
    },
    getMonth(timeString) {
      if (!timeString) return '';
      const date = new Date(timeString);
      return (date.getMonth() + 1).toString().padStart(2, '0');
    },
    getDay(timeString) {
      if (!timeString) return '';
      const date = new Date(timeString);
      return date.getDate().toString().padStart(2, '0');
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 全新轮播图样式 */
.hero-banner {
  position: relative;
  padding: 40px 20px 80px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-container {
  position: relative;
  width: 100%;
  max-width: 1200px;
  height: 500px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  margin: 0 auto;
}

.slide-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
}

.slide.active {
  opacity: 1;
}

.slide-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.4) 0%,
    rgba(0, 0, 0, 0.2) 50%,
    rgba(0, 0, 0, 0.4) 100%
  );
  z-index: 1;
}

.slide-content {
  text-align: center;
  color: white;
  z-index: 2;
  position: relative;
  max-width: 800px;
  padding: 0 40px;
}

.slide-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 25px;
  line-height: 1.2;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: 2px;
  animation: slideInUp 1s ease-out;
}

.slide-subtitle {
  font-size: 20px;
  font-weight: 400;
  line-height: 1.6;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 1px;
  animation: slideInUp 1s ease-out 0.3s both;
}

/* 导航按钮 */
.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: #2c3e50;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 3;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.nav-btn:hover {
  background: white;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.nav-btn.prev {
  left: 20px;
}

.nav-btn.next {
  right: 20px;
}

/* 指示器 */
.indicators {
  position: absolute;
  bottom: 25px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 3;
  background: rgba(255, 255, 255, 0.1);
  padding: 12px 20px;
  border-radius: 25px;
  backdrop-filter: blur(10px);
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.4s ease;
}

.indicator.active {
  background: white;
  transform: scale(1.3);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.5);
}

.indicator:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.2);
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 主内容区域 */
.main-content {
  background: #f8f9fa;
  padding: 60px 0;
}

/* 快速导航 */
.quick-nav {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto 80px;
  padding: 0 20px;
}

.nav-item {
  background: white;
  padding: 40px 30px;
  border-radius: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.nav-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.nav-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
}

.adopt-icon { background: linear-gradient(45deg, #FF6B6B, #FF8E53); }
.rescue-icon { background: linear-gradient(45deg, #4ECDC4, #44A08D); }
.lost-icon { background: linear-gradient(45deg, #A8E6CF, #7FCDCD); }
.forum-icon { background: linear-gradient(45deg, #FFD93D, #FF6B6B); }

.nav-item h3 {
  color: #2c3e50;
  font-size: 20px;
  margin-bottom: 10px;
  font-weight: 600;
}

.nav-item p {
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.5;
}

/* 通用部分样式 */
.section {
  max-width: 1200px;
  margin: 0 auto 80px;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 50px;
}

.section-title {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 700;
}

.section-title i {
  margin-right: 10px;
  color: #3498db;
}

.section-subtitle {
  font-size: 16px;
  color: #7f8c8d;
}

/* 公告部分 */
.announcement-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.announcement-card {
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.announcement-card:hover {
  transform: translateY(-5px);
}

.announcement-item {
  padding: 10px;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.announcement-title {
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.announcement-time {
  color: #95a5a6;
  font-size: 12px;
}

.announcement-content-text {
  color: #5a6c7d;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  color: #3498db;
  font-weight: 500;
  transition: all 0.3s ease;
}

.read-more:hover {
  color: #2980b9;
  transform: translateX(3px);
}

/* 公告详情样式 */
.notice-detail {
  padding: 20px 0;
}

.notice-content {
  font-size: 16px;
  line-height: 1.8;
  color: #2c3e50;
  text-align: justify;
  white-space: pre-line;
}

/* 宠物知识部分 */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.knowledge-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.knowledge-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.knowledge-image {
  height: 200px;
  overflow: hidden;
}

.knowledge-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.knowledge-card:hover .knowledge-image img {
  transform: scale(1.1);
}

.knowledge-content {
  padding: 20px;
}

.knowledge-title {
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-meta {
  display: flex;
  justify-content: space-between;
  color: #95a5a6;
  font-size: 12px;
}

.knowledge-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 活动部分 */
.activity-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.activity-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.activity-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.activity-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.activity-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .activity-image img {
  transform: scale(1.1);
}

.activity-date {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  padding: 10px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.activity-date .month {
  display: block;
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.activity-date .day {
  display: block;
  font-size: 18px;
  color: #2c3e50;
  font-weight: 700;
}

.activity-info {
  padding: 20px;
}

.activity-title {
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-details p {
  color: #7f8c8d;
  font-size: 14px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.activity-footer {
  margin-top: 15px;
  text-align: right;
}

.join-btn {
  background: linear-gradient(45deg, #FF6B6B, #FF8E53);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 500;
}

/* 统计部分 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.stat-card {
  background: white;
  padding: 40px 30px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  background: linear-gradient(45deg, #667eea, #764ba2);
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  color: #7f8c8d;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-banner {
    padding: 20px 10px 60px;
    min-height: 500px;
  }
  
  .banner-container {
    height: 400px;
    border-radius: 15px;
  }
  
  .slide-content {
    padding: 0 20px;
  }
  
  .slide-title {
    font-size: 32px;
    margin-bottom: 20px;
    letter-spacing: 1px;
  }
  
  .slide-subtitle {
    font-size: 16px;
  }
  
  .nav-btn {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .nav-btn.prev {
    left: 10px;
  }
  
  .nav-btn.next {
    right: 10px;
  }
  
  .indicators {
    bottom: 15px;
    padding: 10px 15px;
    gap: 10px;
  }
  
  .indicator {
    width: 8px;
    height: 8px;
  }
  
  .quick-nav {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .announcement-content,
  .knowledge-grid,
  .activity-list,
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>