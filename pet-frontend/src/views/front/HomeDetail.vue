<template>
  <div class="pet-detail-container">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-section">
      <el-breadcrumb separator=">">
        <el-breadcrumb-item :to="{ path: '/front/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/front/adopt' }">宠物领养</el-breadcrumb-item>
        <el-breadcrumb-item>{{ animal.nickname || '宠物详情' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 宠物信息卡片 -->
      <div class="pet-info-card">
        <div class="pet-header">
          <div class="pet-image-section">
            <div class="image-container">
              <img :src="$store.state.baseApi+animal.img" :alt="animal.nickname" class="pet-main-image">
              <div class="image-overlay">
                <div class="overlay-content">
                  <i class="el-icon-camera"></i>
                  <span>高清大图</span>
                </div>
              </div>
            </div>
            <!-- 图片缩略图 -->
            <div class="image-thumbnails">
              <div class="thumbnail-item active">
                <img :src="$store.state.baseApi+animal.img" :alt="animal.nickname">
              </div>
            </div>
          </div>

          <div class="pet-info-section">
            <div class="pet-title-area">
              <h1 class="pet-name">
                <i class="el-icon-star-on pet-icon"></i>
                {{ animal.nickname }}
                <span class="pet-id">#{{ animal.id }}</span>
              </h1>
              <div class="pet-status">
                <span class="status-tag available" v-if="animal.adopt === '可领养'">
                  <i class="el-icon-check"></i>
                  可领养
                </span>
                <span class="status-tag unavailable" v-else>
                  <i class="el-icon-close"></i>
                  不可领养
                </span>
              </div>
            </div>

            <!-- 基本信息 -->
            <div class="basic-info">
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-icon gender-icon" :class="animal.sex === '公' ? 'male' : 'female'">
                    <i :class="animal.sex === '公' ? 'el-icon-male' : 'el-icon-female'"></i>
                  </div>
                  <div class="info-content">
                    <span class="info-label">性别</span>
                    <span class="info-value" :class="animal.sex === '公' ? 'male-text' : 'female-text'">{{ animal.sex }}</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon type-icon">
                    <i class="el-icon-collection-tag"></i>
                  </div>
                  <div class="info-content">
                    <span class="info-label">品种</span>
                    <span class="info-value">{{ animal.type }}</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon age-icon">
                    <i class="el-icon-time"></i>
                  </div>
                  <div class="info-content">
                    <span class="info-label">年龄</span>
                    <span class="info-value">{{ animal.age }}</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon location-icon">
                    <i class="el-icon-location"></i>
                  </div>
                  <div class="info-content">
                    <span class="info-label">活动范围</span>
                    <span class="info-value">{{ animal.address }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 健康状态 -->
            <div class="health-section">
              <h3 class="section-title">
                <i class="el-icon-first-aid-kit"></i>
                健康状态
              </h3>
              <div class="health-grid">
                <div class="health-item">
                  <span class="health-label">身体状况</span>
                  <span class="health-value healthy">{{ animal.status }}</span>
                </div>
                <div class="health-item">
                  <span class="health-label">绝育状态</span>
                  <span class="health-value" :class="animal.sterilization === '是' ? 'status-good' : 'status-pending'">
                    {{ animal.sterilization === '是' ? '✓ 已绝育' : '✗ 未绝育' }}
                  </span>
                </div>
                <div class="health-item">
                  <span class="health-label">疫苗接种</span>
                  <span class="health-value" :class="animal.vaccine === '已接种' ? 'status-good' : 'status-pending'">
                    {{ animal.vaccine }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 描述信息 -->
            <div class="description-section">
              <h3 class="section-title">
                <i class="el-icon-document"></i>
                详细介绍
              </h3>
              <p class="description-text">
                {{ animal.information || '这是一只非常可爱的小天使，性格温顺，等待着有爱心的主人给它一个温暖的家。它会成为您最忠实的伙伴，用纯真的眼神和无条件的爱陪伴您度过每一天。' }}
              </p>
            </div>

            <!-- 互动区域 -->
            <div class="interaction-section">
              <div class="like-section">
                <el-button 
                  :class="['like-btn', { 'liked': praise }]" 
                  @click="praiseClick"
                  :disabled="praise">
                  <i class="el-icon-heart"></i>
                  <span>{{ praise ? '已点赞' : '点赞' }}</span>
                  <span class="like-count">{{ animal.praise || 0 }}</span>
                </el-button>
              </div>
              
              <div class="action-buttons">
                <el-button type="primary" size="large" class="adopt-btn" @click="handleAdopt">
                  <i class="el-icon-user"></i>
                  申请领养
                </el-button>
                <el-button size="large" class="share-btn" @click="handleShare">
                  <i class="el-icon-share"></i>
                  分享给朋友
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论区域 -->
      <div class="comments-section">
        <div class="comments-header">
          <h2 class="comments-title">
            <i class="el-icon-chat-line-round"></i>
            爱心留言
            <span class="comments-count">({{ comments.length }})</span>
          </h2>
          <p class="comments-subtitle">分享您的想法，为小家伙加油鼓劲</p>
        </div>

        <!-- 发表评论 -->
        <div class="comment-form">
          <div class="form-header">
            <div class="user-avatar">
              <img :src="user.avatarUrl ? $store.state.baseApi + user.avatarUrl : '/static/default-avatar.png'" :alt="user.nickname">
            </div>
            <div class="form-content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="3"
                placeholder="说点什么为这个小天使加油吧..."
                class="comment-textarea">
              </el-input>
            </div>
          </div>
          <div class="form-footer">
            <el-button type="primary" @click="saveComment" class="submit-comment-btn">
              <i class="el-icon-edit"></i>
              发表留言
            </el-button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-for="item in comments" :key="item.id" class="comment-item">
            <div class="comment-main">
              <div class="comment-avatar">
                <img :src="$store.state.baseApi+item.avatar" :alt="item.user">
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-user">{{ item.user }}</span>
                  <span class="comment-time">
                    <i class="el-icon-time"></i>
                    {{ item.time }}
                  </span>
                </div>
                <div class="comment-text">{{ item.content }}</div>
                <div class="comment-actions">
                  <el-button type="text" @click="handleAddComment(item.id)" class="reply-btn">
                    <i class="el-icon-chat-line-round"></i>
                    回复
                  </el-button>
                  <el-button 
                    v-if="user.nickname === item.user" 
                    type="text" 
                    @click="delComment(item.id)" 
                    class="delete-btn">
                    <i class="el-icon-delete"></i>
                    删除
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 回复列表 -->
            <div v-if="item.children && item.children.length" class="replies-list">
              <div v-for="subitem in item.children" :key="subitem.id" class="reply-item">
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="reply-user">{{ subitem.user }}</span>
                    <span class="reply-time">
                      <i class="el-icon-time"></i>
                      {{ subitem.time }}
                    </span>
                  </div>
                  <div class="reply-text">{{ subitem.content }}</div>
                  <div class="reply-actions">
                    <el-button 
                      v-if="user.nickname === subitem.user" 
                      type="text" 
                      @click="delComment(subitem.id)" 
                      class="delete-btn">
                      <i class="el-icon-delete"></i>
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="!comments.length" class="empty-comments">
            <div class="empty-icon">
              <i class="el-icon-chat-line-round"></i>
            </div>
            <h3>还没有留言</h3>
            <p>成为第一个为{{ animal.nickname }}留言的人吧！</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 回复对话框 -->
    <el-dialog 
      title="💬 回复留言" 
      :visible.sync="dialogFormVisible" 
      width="500px" 
      class="reply-dialog">
      <el-form label-width="80px">
        <el-form-item label="回复内容">
          <el-input 
            type="textarea" 
            v-model="form.contentr" 
            :rows="4"
            placeholder="请输入您的回复内容...">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveComment">
          <i class="el-icon-check"></i>
          发送回复
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "HomeDetail",
  data() {
    return {
      id: this.$route.query.id,
      animal: {},
      comments: [],
      form: {},
      dialogFormVisible: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      praise: false
    }
  },
  created() {
    this.load()
    this.loadComment()
  },
  methods: {
    handleAdopt() {
      // 跳转到领养页面，并预填宠物ID
      this.$router.push('/front/adopt')
    },
    handleShare() {
      // 分享功能
      if (navigator.share) {
        navigator.share({
          title: `可爱的${this.animal.nickname}等待领养`,
          text: `来看看这只可爱的${this.animal.type}，名字叫${this.animal.nickname}，它需要一个温暖的家！`,
          url: window.location.href
        })
      } else {
        // 复制链接到剪贴板
        const url = window.location.href
        navigator.clipboard.writeText(url).then(() => {
          this.$message.success('链接已复制到剪贴板')
        })
      }
    },
    praiseClick() {
      if(this.praise) {
        return
      }
      this.animal.praise += 1
      this.request.post("/animal", this.animal).then(res => {
        this.load()
        this.praise = true
      })
    },
    load() {
      this.request.get("/animal/" + this.id).then(res => {
        this.animal = res.data
      })
    },
    loadComment() {
      this.request.get("/comment/article/1/" + this.id).then(res => {
        this.comments = res.data
      })
    },
    handleAddComment(pid) {
      this.dialogFormVisible = true
      this.form = {pid: pid}
    },
    saveComment() {
      if (!this.user.username) {
        this.$message.warning("请登录后评论")
        return
      }
      if (this.form.contentr) {
        this.form.content = this.form.contentr
      }
      if (!this.form.content) {
        this.$message.warning("请输入评论")
        return
      }
      this.form.type = 1
      this.form.articleId = this.id
      this.request.post("/comment", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("回复成功")
          this.dialogFormVisible = false
          this.form = {}  // 清空数据
          this.loadComment()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    delComment(id) {
      this.request.delete("/comment/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.loadComment()
        } else {
          this.$message.error("删除失败")
        }
      })
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.pet-detail-container {
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 0;
}

/* 面包屑导航 */
.breadcrumb-section {
  background: white;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 0 0 15px 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.breadcrumb-section .el-breadcrumb {
  font-size: 14px;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 宠物信息卡片 */
.pet-info-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.pet-header {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 30px;
  padding: 30px;
}

/* 图片区域 */
.pet-image-section {
  position: sticky;
  top: 20px;
}

.image-container {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  margin-bottom: 15px;
}

.pet-main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.image-container:hover .image-overlay {
  opacity: 1;
}

.image-container:hover .pet-main-image {
  transform: scale(1.05);
}

.overlay-content {
  color: white;
  text-align: center;
  font-weight: 600;
}

.overlay-content i {
  font-size: 24px;
  margin-bottom: 8px;
  display: block;
}

.image-thumbnails {
  display: flex;
  gap: 10px;
}

.thumbnail-item {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.thumbnail-item.active {
  border-color: #667eea;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 宠物信息区域 */
.pet-info-section {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.pet-title-area {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.pet-name {
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 15px;
  line-height: 1.2;
}

.pet-icon {
  color: #ffd700;
  font-size: 40px;
}

.pet-id {
  font-size: 16px;
  color: #7f8c8d;
  font-weight: 400;
  background: #ecf0f1;
  padding: 4px 12px;
  border-radius: 20px;
}

.pet-status {
  margin-top: 10px;
}

.status-tag {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-tag.available {
  background: linear-gradient(135deg, #00b894, #00cec9);
  color: white;
}

.status-tag.unavailable {
  background: linear-gradient(135deg, #e17055, #fdcb6e);
  color: white;
}

/* 基本信息 */
.basic-info {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 15px;
  border-left: 4px solid #667eea;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.info-icon {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
}

.info-icon.gender-icon.male {
  background: linear-gradient(135deg, #74b9ff, #0984e3);
}

.info-icon.gender-icon.female {
  background: linear-gradient(135deg, #fd79a8, #e84393);
}

.info-icon.type-icon {
  background: linear-gradient(135deg, #a29bfe, #6c5ce7);
}

.info-icon.age-icon {
  background: linear-gradient(135deg, #ffeaa7, #fdcb6e);
}

.info-icon.location-icon {
  background: linear-gradient(135deg, #55a3ff, #003d82);
}

.info-content {
  flex: 1;
}

.info-label {
  display: block;
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
  margin-bottom: 4px;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.info-value.male-text {
  color: #0984e3;
}

.info-value.female-text {
  color: #e84393;
}

/* 健康状态 */
.health-section {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 15px;
  border-left: 4px solid #00b894;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #00b894;
}

.health-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.health-item {
  text-align: center;
  padding: 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.health-label {
  display: block;
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
  margin-bottom: 8px;
}

.health-value {
  font-size: 14px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 15px;
  display: inline-block;
}

.health-value.healthy {
  background: linear-gradient(135deg, #00b894, #00cec9);
  color: white;
}

.health-value.status-good {
  background: linear-gradient(135deg, #00b894, #00cec9);
  color: white;
}

.health-value.status-pending {
  background: linear-gradient(135deg, #fdcb6e, #e17055);
  color: white;
}

/* 描述信息 */
.description-section {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 15px;
  border-left: 4px solid #fd79a8;
}

.description-section .section-title i {
  color: #fd79a8;
}

.description-text {
  font-size: 16px;
  line-height: 1.8;
  color: #2c3e50;
  margin: 0;
  text-align: justify;
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

/* 互动区域 */
.interaction-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  color: white;
}

.like-section {
  display: flex;
  align-items: center;
}

.like-btn {
  background: rgba(255,255,255,0.2);
  border: 2px solid rgba(255,255,255,0.3);
  color: white;
  padding: 12px 20px;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.like-btn:hover {
  background: rgba(255,255,255,0.3);
  transform: translateY(-2px);
}

.like-btn.liked {
  background: rgba(255,255,255,0.8);
  color: #e74c3c;
}

.like-count {
  background: rgba(255,255,255,0.2);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-left: 5px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.adopt-btn {
  background: linear-gradient(135deg, #fd79a8, #e84393);
  border: none;
  color: white;
  padding: 15px 30px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(232, 67, 147, 0.3);
}

.adopt-btn:hover {
  background: linear-gradient(135deg, #e84393, #fd79a8);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(232, 67, 147, 0.4);
}

.share-btn {
  background: rgba(255,255,255,0.2);
  border: 2px solid rgba(255,255,255,0.3);
  color: white;
  padding: 15px 30px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
}

.share-btn:hover {
  background: rgba(255,255,255,0.3);
  transform: translateY(-2px);
}

/* 评论区域 */
.comments-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.comments-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.comments-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.comments-title i {
  color: #667eea;
}

.comments-count {
  font-size: 18px;
  color: #7f8c8d;
  font-weight: 400;
}

.comments-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 10px 0 0 0;
}

/* 评论表单 */
.comment-form {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 15px;
  margin-bottom: 30px;
}

.form-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-content {
  flex: 1;
}

.comment-textarea {
  border-radius: 10px;
}

.comment-textarea .el-textarea__inner {
  border: 2px solid #e0e6ed;
  border-radius: 10px;
  padding: 15px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.comment-textarea .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.form-footer {
  text-align: right;
}

.submit-comment-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
  padding: 12px 25px;
  border-radius: 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.submit-comment-btn:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 20px;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background: #f1f3f5;
  transform: translateY(-2px);
}

.comment-main {
  display: flex;
  gap: 15px;
}

.comment-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: 600;
  color: #667eea;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #2c3e50;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

.reply-btn {
  color: #667eea;
  font-size: 12px;
  padding: 4px 8px;
}

.reply-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  border-radius: 4px;
}

.delete-btn {
  color: #e74c3c;
  font-size: 12px;
  padding: 4px 8px;
}

.delete-btn:hover {
  background: rgba(231, 76, 60, 0.1);
  border-radius: 4px;
}

/* 回复列表 */
.replies-list {
  margin-top: 15px;
  padding-left: 60px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reply-item {
  background: white;
  border-radius: 10px;
  padding: 15px;
  border-left: 3px solid #667eea;
}

.reply-content {
  font-size: 13px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.reply-user {
  font-weight: 600;
  color: #667eea;
  font-size: 13px;
}

.reply-time {
  font-size: 11px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 3px;
}

.reply-text {
  color: #2c3e50;
  line-height: 1.5;
  margin-bottom: 8px;
}

.reply-actions {
  text-align: right;
}

/* 空状态 */
.empty-comments {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-comments h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #2c3e50;
}

.empty-comments p {
  font-size: 14px;
  margin: 0;
}

/* 对话框 */
.reply-dialog .el-dialog {
  border-radius: 15px;
}

.reply-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 20px 25px;
  border-radius: 15px 15px 0 0;
}

.reply-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pet-header {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .pet-image-section {
    position: static;
  }
  
  .image-container {
    max-width: 400px;
    margin: 0 auto 15px;
  }
  
  .pet-name {
    font-size: 28px;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .health-grid {
    grid-template-columns: 1fr;
  }
  
  .interaction-section {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: center;
  }
  
  .form-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .replies-list {
    padding-left: 20px;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 0 10px;
  }
  
  .pet-info-card,
  .comments-section {
    padding: 20px 15px;
  }
  
  .pet-name {
    font-size: 24px;
  }
  
  .comments-title {
    font-size: 24px;
    flex-direction: column;
    gap: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
    width: 100%;
  }
  
  .adopt-btn,
  .share-btn {
    width: 100%;
  }
}
</style>