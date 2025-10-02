<template>
  <div class="my-adopt-container">
    <!-- 页面标题区域 -->
    <div class="header-section">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-heart"></i>
            我的领养申请
          </h1>
          <p class="page-subtitle">记录您的每一份爱心，见证温暖的传递</p>
        </div>
        
        <!-- 统计卡片 -->
        <div class="stats-cards">
          <div class="stat-card pending">
            <div class="stat-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ pendingCount }}</div>
              <div class="stat-label">审核中</div>
            </div>
          </div>
          <div class="stat-card approved">
            <div class="stat-icon">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ approvedCount }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
          <div class="stat-card total">
            <div class="stat-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ totalCount }}</div>
              <div class="stat-label">总申请</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 空状态 -->
      <div v-if="!tableData.length" class="empty-state">
        <div class="empty-illustration">
          <div class="empty-icon">
            <i class="el-icon-document-add"></i>
          </div>
          <h3 class="empty-title">还没有领养申请</h3>
          <p class="empty-desc">赶快去看看那些可爱的小家伙吧，说不定会有意想不到的缘分</p>
          <el-button type="primary" icon="el-icon-plus" class="goto-adopt-btn" @click="$router.push('/front/adopt')">
            去领养宠物
          </el-button>
        </div>
      </div>

      <!-- 申请记录列表 -->
      <div v-else class="adopt-records">
        <div v-for="item in tableData" :key="item.id" class="adopt-record-card">
          <!-- 卡片头部 -->
          <div class="card-header">
            <div class="record-status">
              <div class="status-badge" :class="getStatusClass(item.state)">
                <i :class="getStatusIcon(item.state)"></i>
                <span>{{ getStatusText(item.state) }}</span>
              </div>
              <div class="submit-time">
                <i class="el-icon-time"></i>
                申请时间：{{ item.createTime || '2024-01-15' }}
              </div>
            </div>
          </div>

          <!-- 宠物信息区域 -->
          <div class="pet-section">
            <div class="pet-image-container">
              <img :src="$store.state.baseApi+item.animal.img" :alt="item.animal.nickname" class="pet-image">
              <div class="image-overlay" @click="$router.push('/front/homeDetail?id=' + item.animal.id)">
                <span class="view-detail">
                  <i class="el-icon-view"></i>
                  查看详情
                </span>
              </div>
            </div>
            
            <div class="pet-info">
              <div class="pet-header">
                <h3 class="pet-name">
                  <i class="el-icon-star-on"></i>
                  {{ item.animal.nickname }}
                </h3>
                <div class="pet-basic-tags">
                  <span class="pet-tag gender" :class="item.animal.sex === '公' ? 'male' : 'female'">
                    <i :class="item.animal.sex === '公' ? 'el-icon-male' : 'el-icon-female'"></i>
                    {{ item.animal.sex }}
                  </span>
                  <span class="pet-tag age">
                    <i class="el-icon-time"></i>
                    {{ item.animal.age }}
                  </span>
                </div>
              </div>
              
              <div class="pet-details">
                <div class="detail-row">
                  <div class="detail-item">
                    <span class="detail-label">绝育状态</span>
                    <span class="detail-value" :class="item.animal.sterilization === '是' ? 'status-good' : 'status-pending'">
                      {{ item.animal.sterilization === '是' ? '✓ 已绝育' : '✗ 未绝育' }}
                    </span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">疫苗接种</span>
                    <span class="detail-value" :class="item.animal.vaccine === '已接种' ? 'status-good' : 'status-pending'">
                      {{ item.animal.vaccine }}
                    </span>
                  </div>
                </div>
                <div class="detail-row">
                  <div class="detail-item">
                    <span class="detail-label">健康状态</span>
                    <span class="detail-value status-health">{{ item.animal.status }}</span>
                  </div>
                  <div class="detail-item full-width">
                    <span class="detail-label">描述</span>
                    <span class="detail-value">{{ item.animal.information || '这是一只可爱的小天使' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 申请信息区域 -->
          <div class="application-section">
            <div class="section-header">
              <h4 class="section-title">
                <i class="el-icon-user"></i>
                申请信息
              </h4>
              <el-button type="text" @click="toggleDetails(item.id)" class="toggle-btn">
                {{ expandedItems.includes(item.id) ? '收起详情' : '展开详情' }}
                <i :class="expandedItems.includes(item.id) ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
              </el-button>
            </div>
            
            <!-- 基本信息概览 -->
            <div class="application-summary">
              <div class="summary-item">
                <span class="summary-label">申请人</span>
                <span class="summary-value">{{ item.name }}</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">联系电话</span>
                <span class="summary-value">{{ item.phone }}</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">年龄</span>
                <span class="summary-value">{{ item.age }}岁</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">养宠经验</span>
                <span class="summary-value experience" :class="item.experience === '有经验' ? 'experienced' : 'novice'">
                  <i :class="item.experience === '有经验' ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                  {{ item.experience }}
                </span>
              </div>
            </div>
            
            <!-- 详细信息（可收起展开） -->
            <transition name="slide-fade">
              <div v-show="expandedItems.includes(item.id)" class="application-details">
                <div class="details-grid">
                  <div class="detail-group">
                    <h5 class="group-title">
                      <i class="el-icon-user-solid"></i>
                      个人信息
                    </h5>
                    <div class="detail-items">
                      <div class="detail-item-full">
                        <span class="item-label">性别</span>
                        <span class="item-value">{{ item.sex }}</span>
                      </div>
                      <div class="detail-item-full">
                        <span class="item-label">婚姻状况</span>
                        <span class="item-value">{{ item.married }}</span>
                      </div>
                      <div class="detail-item-full">
                        <span class="item-label">职业</span>
                        <span class="item-value">{{ item.profession }}</span>
                      </div>
                      <div class="detail-item-full">
                        <span class="item-label">收入</span>
                        <span class="item-value">{{ item.income }}</span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="detail-group">
                    <h5 class="group-title">
                      <i class="el-icon-location"></i>
                      联系信息
                    </h5>
                    <div class="detail-items">
                      <div class="detail-item-full">
                        <span class="item-label">住址</span>
                        <span class="item-value">{{ item.address }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="reason-section">
                  <h5 class="group-title">
                    <i class="el-icon-chat-line-round"></i>
                    领养理由
                  </h5>
                  <div class="reason-content">
                    <p class="reason-text">{{ item.reason }}</p>
                  </div>
                </div>
              </div>
            </transition>
          </div>

          <!-- 操作区域 -->
          <div class="action-section">
            <div class="action-buttons">
              <el-button 
                type="primary" 
                icon="el-icon-view" 
                @click="$router.push('/front/homeDetail?id=' + item.animal.id)"
                class="action-btn view-btn">
                查看宠物详情
              </el-button>
              <el-button 
                v-if="item.state === '审核通过'" 
                type="success" 
                icon="el-icon-check" 
                class="action-btn success-btn"
                disabled>
                审核已通过
              </el-button>
              <el-button 
                v-else-if="item.state === '审核不通过'" 
                type="danger" 
                icon="el-icon-close" 
                class="action-btn reject-btn"
                disabled>
                审核未通过
              </el-button>
              <el-button 
                v-else 
                type="warning" 
                icon="el-icon-time" 
                class="action-btn pending-btn"
                disabled>
                等待审核中
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MyAdopt",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      nickname: "",
      dialogFormVisible: false,
      form: {},
      expandedItems: [] // 用于控制详情展开收起
    }
  },
  computed: {
    // 审核中的数量
    pendingCount() {
      return this.tableData.filter(item => !item.state || item.state === '审核中').length
    },
    // 审核通过的数量
    approvedCount() {
      return this.tableData.filter(item => item.state === '审核通过').length
    },
    // 总申请数量
    totalCount() {
      return this.tableData.length
    }
  },
  created() {
    this.load()
  },
  methods: {
    // 切换详情展开收起
    toggleDetails(itemId) {
      const index = this.expandedItems.indexOf(itemId)
      if (index > -1) {
        this.expandedItems.splice(index, 1)
      } else {
        this.expandedItems.push(itemId)
      }
    },
    // 获取状态样式类
    getStatusClass(state) {
      switch(state) {
        case '审核通过': return 'approved'
        case '审核不通过': return 'rejected'
        default: return 'pending'
      }
    },
    // 获取状态图标
    getStatusIcon(state) {
      switch(state) {
        case '审核通过': return 'el-icon-circle-check'
        case '审核不通过': return 'el-icon-circle-close'
        default: return 'el-icon-clock'
      }
    },
    // 获取状态文本
    getStatusText(state) {
      switch(state) {
        case '审核通过': return '审核通过'
        case '审核不通过': return '审核不通过'
        default: return '审核中'
      }
    },
    save() {
      this.request.post("/applcation", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleApply(animalId) {
      this.form = {animalId: animalId}
      this.dialogFormVisible = true
    },
    load() {
      this.request.get("/applcation/my").then(res => {
        this.tableData = res.data
      })
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
  }
}
</script>

<style scoped>
/* 页面容器 */
.my-adopt-container {
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
}

/* 页面标题区域 */
.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
}

.header-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 1000"><defs><radialGradient id="a" cx=".5" cy=".5" r=".5"><stop offset="0%" stop-color="%23ffffff" stop-opacity=".1"/><stop offset="100%" stop-color="%23ffffff" stop-opacity="0"/></radialGradient></defs><circle cx="250" cy="250" r="120" fill="url(%23a)"/><circle cx="750" cy="250" r="80" fill="url(%23a)"/><circle cx="250" cy="750" r="100" fill="url(%23a)"/><circle cx="750" cy="750" r="150" fill="url(%23a)"/></svg>') repeat;
  animation: float 20s ease-in-out infinite;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translateX(0) translateY(0) rotate(0deg); }
  33% { transform: translateX(10px) translateY(-10px) rotate(120deg); }
  66% { transform: translateX(-5px) translateY(5px) rotate(240deg); }
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.title-section {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 3rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.page-title i {
  font-size: 3.5rem;
  color: #fd79a8;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

.page-subtitle {
  font-size: 1.2rem;
  color: rgba(255,255,255,0.9);
  margin: 10px 0 0 0;
  font-weight: 300;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.stat-card {
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid rgba(255,255,255,0.2);
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255,255,255,0.25);
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-card.pending .stat-icon {
  background: linear-gradient(135deg, #ffeaa7, #fdcb6e);
}

.stat-card.approved .stat-icon {
  background: linear-gradient(135deg, #00b894, #00cec9);
}

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #a29bfe, #6c5ce7);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: white;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  margin-top: 5px;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.empty-illustration {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 120px;
  color: #ddd;
  margin-bottom: 30px;
}

.empty-title {
  font-size: 24px;
  color: #2c3e50;
  margin-bottom: 15px;
  font-weight: 600;
}

.empty-desc {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0 0 30px 0;
  line-height: 1.6;
}

.goto-adopt-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 25px;
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.goto-adopt-btn:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 申请记录列表 */
.adopt-records {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.adopt-record-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.adopt-record-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0,0,0,0.15);
}

/* 卡片头部 */
.card-header {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  padding: 20px 25px;
  border-bottom: 1px solid #f0f0f0;
}

.record-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.status-badge.pending {
  background: linear-gradient(135deg, #ffeaa7, #fdcb6e);
  color: #2d3436;
}

.status-badge.approved {
  background: linear-gradient(135deg, #00b894, #00cec9);
  color: white;
}

.status-badge.rejected {
  background: linear-gradient(135deg, #e17055, #fd79a8);
  color: white;
}

.submit-time {
  font-size: 12px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 宠物信息区域 */
.pet-section {
  padding: 25px;
  display: flex;
  gap: 25px;
}

.pet-image-container {
  position: relative;
  width: 200px;
  height: 200px;
  border-radius: 15px;
  overflow: hidden;
  flex-shrink: 0;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.pet-image-container:hover .image-overlay {
  opacity: 1;
}

.pet-image-container:hover .pet-image {
  transform: scale(1.1);
}

.view-detail {
  color: white;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.pet-info {
  flex: 1;
}

.pet-header {
  margin-bottom: 20px;
}

.pet-name {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.pet-name i {
  color: #ffd700;
}

.pet-basic-tags {
  display: flex;
  gap: 10px;
}

.pet-tag {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 5px;
}

.pet-tag.gender.male {
  background: linear-gradient(135deg, #74b9ff, #0984e3);
  color: white;
}

.pet-tag.gender.female {
  background: linear-gradient(135deg, #fd79a8, #e84393);
  color: white;
}

.pet-tag.age {
  background: linear-gradient(135deg, #ffeaa7, #fdcb6e);
  color: #2d3436;
}

.pet-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-row {
  display: flex;
  gap: 20px;
}

.detail-item {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-item.full-width {
  flex: 2;
}

.detail-label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 600;
}

.detail-value {
  font-size: 13px;
  font-weight: 600;
}

.detail-value.status-good {
  color: #00b894;
}

.detail-value.status-pending {
  color: #fdcb6e;
}

.detail-value.status-health {
  color: #0984e3;
}

/* 申请信息区域 */
.application-section {
  border-top: 1px solid #f0f0f0;
  padding: 25px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #667eea;
}

.toggle-btn {
  color: #667eea;
  font-size: 12px;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  background: rgba(102, 126, 234, 0.1);
}

.application-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.summary-item {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 600;
}

.summary-value {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.summary-value.experience {
  display: flex;
  align-items: center;
  gap: 5px;
}

.summary-value.experience.experienced {
  color: #00b894;
}

.summary-value.experience.novice {
  color: #fdcb6e;
}

/* 详细信息 */
.application-details {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 25px;
  margin-top: 15px;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
  margin-bottom: 25px;
}

.detail-group {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.group-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.group-title i {
  color: #667eea;
}

.detail-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-item-full {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.item-label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 600;
}

.item-value {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 500;
}

.reason-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.reason-content {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  border-left: 4px solid #667eea;
}

.reason-text {
  font-size: 14px;
  line-height: 1.6;
  color: #2c3e50;
  margin: 0;
  font-style: italic;
}

/* 操作区域 */
.action-section {
  background: #f8f9fa;
  padding: 20px 25px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.action-btn {
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.view-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
}

.view-btn:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
}

.success-btn {
  background: linear-gradient(135deg, #00b894, #00cec9);
  border: none;
  color: white;
}

.reject-btn {
  background: linear-gradient(135deg, #e17055, #fd79a8);
  border: none;
  color: white;
}

.pending-btn {
  background: linear-gradient(135deg, #ffeaa7, #fdcb6e);
  border: none;
  color: #2d3436;
}

/* 动画效果 */
.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .page-title i {
    font-size: 2.5rem;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .pet-section {
    flex-direction: column;
    gap: 20px;
  }
  
  .pet-image-container {
    width: 100%;
    height: 250px;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .application-summary {
    grid-template-columns: 1fr;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .action-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .header-section {
    padding: 30px 15px;
  }
  
  .page-title {
    font-size: 1.8rem;
    flex-direction: column;
    gap: 10px;
  }
  
  .main-content {
    padding: 0 15px;
  }
  
  .adopt-record-card {
    margin: 0 5px;
  }
  
  .card-header,
  .pet-section,
  .application-section,
  .action-section {
    padding: 20px 15px;
  }
  
  .record-status {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>
