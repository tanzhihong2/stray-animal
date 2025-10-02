<template>
  <div class="adopt-container">
    <!-- 页面标题和搜索区域 -->
    <div class="header-section">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-star-on"></i>
            宠物领养
          </h1>
          <p class="page-subtitle">每一个小生命都值得被温柔以待，给它们一个温暖的家</p>
        </div>
        
        <!-- 搜索和筛选区域 -->
        <div class="search-section">
          <div class="search-box">
            <el-input 
              v-model="nickname" 
              placeholder="🔍 搜索可爱的小伙伴..." 
              size="large" 
              class="search-input"
              clearable>
              <el-button slot="append" icon="el-icon-search" @click="load()">搜索</el-button>
            </el-input>
          </div>
          <div class="action-buttons">
            <el-button 
              type="success" 
              icon="el-icon-view" 
              class="my-adopt-btn"
              @click="$router.push('/front/myAdopt')">
              查看我的领养
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 宠物卡片列表 -->
    <div class="pets-grid">
      <div 
        v-for="item in tableData" 
        :key="item.id" 
        class="pet-card"
        @click="$router.push('/front/homeDetail?id=' + item.id)">
        
        <!-- 宠物图片 -->
        <div class="pet-image-container">
          <img :src="$store.state.baseApi+item.img" alt="宠物照片" class="pet-image">
          <div class="image-overlay">
            <span class="view-detail">查看详情</span>
          </div>
        </div>
        
        <!-- 宠物信息 -->
        <div class="pet-info">
          <div class="pet-header">
            <h3 class="pet-name">{{ item.nickname }}</h3>
            <div class="pet-basic-info">
              <span class="info-tag gender" :class="item.sex === '公' ? 'male' : 'female'">
                <i :class="item.sex === '公' ? 'el-icon-male' : 'el-icon-female'"></i>
                {{ item.sex }}
              </span>
              <span class="info-tag age">
                <i class="el-icon-time"></i>
                {{ item.age }}
              </span>
            </div>
          </div>
          
          <div class="pet-details">
            <div class="detail-item">
              <span class="detail-label">绝育状态</span>
              <span class="detail-value" :class="item.sterilization === '是' ? 'status-yes' : 'status-no'">
                {{ item.sterilization === '是' ? '✓ 已绝育' : '✗ 未绝育' }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">疫苗接种</span>
              <span class="detail-value" :class="item.vaccine === '已接种' ? 'status-yes' : 'status-no'">
                {{ item.vaccine }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">健康状态</span>
              <span class="detail-value status-health">{{ item.status }}</span>
            </div>
            <div class="detail-item description">
              <span class="detail-label">描述</span>
              <p class="detail-value">{{ item.information || '这是一只可爱的小天使，等待着有爱心的你' }}</p>
            </div>
          </div>
          
          <!-- 申请领养按钮 -->
          <div class="action-section">
            <el-button 
              type="primary" 
              icon="el-icon-heart" 
              class="adopt-btn"
              @click.stop="handleApply(item.id)">
              申请领养
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!tableData.length" class="empty-state">
      <img src="/static/empty-pets.svg" alt="暂无宠物" class="empty-image">
      <h3 class="empty-title">暂时没有找到小伙伴</h3>
      <p class="empty-desc">请尝试调整搜索条件，或稍后再来看看</p>
    </div>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[6, 12, 18, 24]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background>
      </el-pagination>
    </div>

    <!-- 领养申请弹窗 -->
    <el-dialog 
      title="💝 领养申请" 
      :visible.sync="dialogFormVisible" 
      width="500px" 
      :close-on-click-modal="false"
      class="adopt-dialog">
      
      <div class="dialog-header">
        <p class="dialog-subtitle">请认真填写以下信息，我们会仔细审核您的申请</p>
      </div>
      
      <el-form 
        ref="adoptForm" 
        :model="form" 
        :rules="rules" 
        label-width="100px" 
        class="adopt-form">
        
        <div class="form-section">
          <h4 class="section-title">个人信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input 
                  v-model="form.name" 
                  placeholder="请输入您的真实姓名"
                  prefix-icon="el-icon-user">
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input 
                  v-model="form.age" 
                  placeholder="请输入您的年龄" 
                  type="number"
                  prefix-icon="el-icon-time">
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="form.sex" class="radio-group">
                  <el-radio label="男" class="custom-radio">👨 男</el-radio>
                  <el-radio label="女" class="custom-radio">👩 女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="婚姻状态" prop="married">
                <el-radio-group v-model="form.married" class="radio-group">
                  <el-radio label="未婚" class="custom-radio">💍 未婚</el-radio>
                  <el-radio label="已婚" class="custom-radio">💑 已婚</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="联系方式" prop="phone">
            <el-input 
              v-model="form.phone" 
              placeholder="请输入11位手机号码" 
              maxlength="11"
              prefix-icon="el-icon-phone">
            </el-input>
          </el-form-item>
        </div>
        
        <div class="form-section">
          <h4 class="section-title">经济状况</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="职业" prop="profession">
                <el-input 
                  v-model="form.profession" 
                  placeholder="请输入您的职业"
                  prefix-icon="el-icon-briefcase">
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="月收入" prop="income">
                <el-input 
                  v-model="form.income" 
                  placeholder="请输入您的月收入（元）"
                  prefix-icon="el-icon-coin">
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <div class="form-section">
          <h4 class="section-title">养宠情况</h4>
          <el-form-item label="养宠经验" prop="experience">
            <el-radio-group v-model="form.experience" class="radio-group">
              <el-radio label="无经验" class="custom-radio">🌱 无经验</el-radio>
              <el-radio label="有经验" class="custom-radio">🏆 有经验</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="住址" prop="address">
            <el-input 
              v-model="form.address" 
              placeholder="请输入您的详细住址"
              prefix-icon="el-icon-location">
            </el-input>
          </el-form-item>
          
          <el-form-item label="领养理由" prop="reason">
            <el-input 
              v-model="form.reason" 
              type="textarea" 
              :rows="4" 
              placeholder="请详细说明您的领养理由，让我们了解您对这个小生命的爱心和责任心...">
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="save" class="submit-btn">
          <i class="el-icon-check"></i>
          提交申请
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "FrontHome",
  data() {
    // 手机号码验证规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入联系电话'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码（11位数字）'))
      } else {
        callback()
      }
    }
    // 年龄验证规则
    const validateAge = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入年龄'))
      } else if (isNaN(value) || value < 18 || value > 100) {
        callback(new Error('年龄必须为18-100之间的数字'))
      } else {
        callback()
      }
    }
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 6,
      nickname: "",
      dialogFormVisible: false,
      form: {},
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        sex: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        age: [
          { validator: validateAge, trigger: 'blur' }
        ],
        experience: [
          { required: true, message: '请选择养宠经验', trigger: 'change' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        married: [
          { required: true, message: '请选择婚姻状态', trigger: 'change' }
        ],
        income: [
          { required: true, message: '请输入收入情况', trigger: 'blur' }
        ],
        profession: [
          { required: true, message: '请输入职业', trigger: 'blur' },
          { min: 2, max: 20, message: '职业长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入住址', trigger: 'blur' },
          { min: 5, max: 100, message: '住址长度在 5 到 100 个字符', trigger: 'blur' }
        ],
        reason: [
          { required: true, message: '请输入领养理由', trigger: 'blur' },
          { min: 10, max: 200, message: '领养理由请详细描述，10-200个字符', trigger: 'blur' }
        ]
      },
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
    }
  },
  created() {
    this.load()
  },
  methods: {
    save() {
      // 表单验证
      this.$refs.adoptForm.validate((valid) => {
        if (valid) {
          this.form.userId = this.user.id
          this.request.post("/applcation", this.form).then(res => {
            if (res.code === '200') {
              this.$message.success("申请提交成功！我们会尽快审核您的申请")
              this.dialogFormVisible = false
              // 重置表单
              this.$refs.adoptForm.resetFields()
              this.form = {}
            } else {
              this.$message.error("提交失败，请稍后重试")
            }
          })
        } else {
          this.$message.error('请完善必填信息')
          return false
        }
      })
    },
    handleApply(animalId) {
      this.form = {animalId: animalId}
      this.dialogFormVisible = true
    },
    load() {
      this.request.get("/animal/page/user", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.nickname,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
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
.adopt-container {
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
  margin-bottom: 30px;
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
  color: #ffd700;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

.page-subtitle {
  font-size: 1.2rem;
  color: rgba(255,255,255,0.9);
  margin: 10px 0 0 0;
  font-weight: 300;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

/* 搜索区域 */
.search-section {
  display: flex;
  gap: 20px;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  max-width: 500px;
}

.search-input {
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.search-input .el-input__inner {
  border-radius: 25px 0 0 25px;
  border: none;
  padding-left: 20px;
  font-size: 16px;
  height: 50px;
  line-height: 50px;
}

.search-input .el-input-group__append {
  background: #409EFF;
  border: none;
  border-radius: 0 25px 25px 0;
}

.search-input .el-input-group__append .el-button {
  border: none;
  background: transparent;
  color: white;
  font-weight: 600;
  padding: 0 25px;
  height: 50px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.my-adopt-btn {
  border-radius: 25px;
  padding: 12px 30px;
  font-weight: 600;
  height: auto;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  border: 2px solid rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.1);
  color: white;
  transition: all 0.3s ease;
}

.my-adopt-btn:hover {
  background: rgba(255,255,255,0.2);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

/* 宠物卡片网格 */
.pets-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
  padding: 0 20px;
}

/* 宠物卡片 */
.pet-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.pet-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0,0,0,0.15);
}

.pet-card:hover .image-overlay {
  opacity: 1;
}

/* 宠物图片容器 */
.pet-image-container {
  position: relative;
  height: 250px;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.pet-card:hover .pet-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 0%, rgba(0,0,0,0.7) 100%);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  padding: 20px;
}

.view-detail {
  color: white;
  font-weight: 600;
  font-size: 16px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

/* 宠物信息 */
.pet-info {
  padding: 25px;
}

.pet-header {
  margin-bottom: 20px;
}

.pet-name {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.pet-basic-info {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.info-tag {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 5px;
}

.info-tag.gender.male {
  background: linear-gradient(135deg, #74b9ff, #0984e3);
  color: white;
}

.info-tag.gender.female {
  background: linear-gradient(135deg, #fd79a8, #e84393);
  color: white;
}

.info-tag.age {
  background: linear-gradient(135deg, #ffeaa7, #fdcb6e);
  color: #2d3436;
}

/* 详情区域 */
.pet-details {
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item.description {
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.detail-label {
  font-weight: 600;
  color: #7f8c8d;
  font-size: 14px;
  min-width: 80px;
}

.detail-value {
  font-size: 14px;
  color: #2c3e50;
  text-align: right;
  flex: 1;
}

.detail-item.description .detail-value {
  text-align: left;
  line-height: 1.5;
  color: #636e72;
  font-style: italic;
}

.status-yes {
  color: #00b894;
  font-weight: 600;
}

.status-no {
  color: #e17055;
  font-weight: 600;
}

.status-health {
  color: #0984e3;
  font-weight: 600;
}

/* 操作区域 */
.action-section {
  text-align: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.adopt-btn {
  width: 100%;
  height: 45px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #fd79a8, #e84393);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(232, 67, 147, 0.3);
}

.adopt-btn:hover {
  background: linear-gradient(135deg, #e84393, #fd79a8);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(232, 67, 147, 0.4);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  margin: 20px;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.empty-image {
  width: 150px;
  height: 150px;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-title {
  font-size: 24px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-desc {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
}

/* 分页器 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

/* 对话框样式 */
.adopt-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.adopt-dialog .el-dialog {
  border-radius: 20px;
}

.adopt-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 25px 30px;
  text-align: center;
}

.adopt-dialog .el-dialog__title {
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.dialog-header {
  text-align: center;
  margin-bottom: 30px;
}

.dialog-subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

/* 表单样式 */
.adopt-form {
  padding: 0 10px;
}

.form-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 15px;
  border-left: 4px solid #667eea;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '📝';
  font-size: 16px;
}

.form-section:nth-child(1) .section-title::before {
  content: '👤';
}

.form-section:nth-child(2) .section-title::before {
  content: '💼';
}

.form-section:nth-child(3) .section-title::before {
  content: '🐶';
}

.radio-group {
  display: flex;
  gap: 15px;
}

.custom-radio {
  margin-right: 0;
  padding: 8px 15px;
  border: 2px solid #e0e6ed;
  border-radius: 20px;
  transition: all 0.3s ease;
  background: white;
}

.custom-radio.is-checked {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.adopt-form .el-input__inner {
  border-radius: 10px;
  border: 2px solid #e0e6ed;
  padding-left: 45px;
  height: 45px;
  transition: all 0.3s ease;
}

.adopt-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.adopt-form .el-input__prefix {
  left: 15px;
  color: #667eea;
}

.adopt-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e0e6ed;
  padding: 15px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  transition: all 0.3s ease;
}

.adopt-form .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* 对话框底部 */
.dialog-footer {
  padding: 20px 30px;
  text-align: center;
  background: #f8f9fa;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.cancel-btn {
  padding: 12px 30px;
  border-radius: 25px;
  border: 2px solid #ddd;
  background: white;
  color: #666;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  border-color: #999;
  color: #333;
}

.submit-btn {
  padding: 12px 30px;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .page-title i {
    font-size: 2.5rem;
  }
  
  .search-section {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .pets-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .pet-card {
    margin: 0 10px;
  }
  
  .adopt-dialog {
    width: 95% !important;
    margin: 0 auto;
  }
  
  .form-section {
    padding: 15px;
  }
  
  .radio-group {
    flex-direction: column;
    gap: 10px;
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
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .pets-grid {
    padding: 0 15px;
  }
  
  .pet-info {
    padding: 20px;
  }
  
  .pet-basic-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .detail-item {
    flex-direction: column;
    gap: 5px;
  }
  
  .detail-value {
    text-align: left;
  }
}
</style>
