<template>
  <div class="salvation-container">
    <!-- 页面头部区域 -->
    <div class="salvation-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-first-aid-kit"></i>
          流浪动物救助信息
        </h1>
        <p class="page-subtitle">每一个小生命都值得被拯救，让我们一起为它们撑起保护伞</p>
      </div>
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd" class="add-btn" size="medium">
          <i class="el-icon-plus"></i>
          新增救助信息
        </el-button>
      </div>
    </div>

    <!-- 救助信息卡片区域 -->
    <div class="rescue-cards" v-if="tableData.length > 0">
      <div class="card-item" v-for="(item, index) in tableData" :key="index">
        <div class="card-header">
          <div class="status-badge" :class="item.state === '已解决' ? 'resolved' : 'pending'">
            <i :class="item.state === '已解决' ? 'el-icon-check' : 'el-icon-warning-outline'"></i>
            {{ item.state || '未解决' }}
          </div>
          <div class="rescue-time">
            <i class="el-icon-time"></i>
            {{ formatTime(item.time) }}
          </div>
        </div>
        
        <div class="card-content">
          <div class="image-section">
            <el-image 
              class="rescue-image"
              :src="$store.state.baseApi + item.img"
              :preview-src-list="[$store.state.baseApi + item.img]"
              fit="cover">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>
          
          <div class="info-section">
            <div class="info-item">
              <h3 class="rescue-title">
                <i class="el-icon-location"></i>
                {{ item.address || '待补充地点信息' }}
              </h3>
            </div>
            
            <div class="info-item">
              <p class="rescue-description">
                <i class="el-icon-document"></i>
                {{ item.information || '暂无详细描述' }}
              </p>
            </div>
            
            <div class="contact-info">
              <div class="contact-item">
                <i class="el-icon-user"></i>
                <span>联系人：{{ item.person || '匿名' }}</span>
              </div>
              <div class="contact-item">
                <i class="el-icon-phone"></i>
                <span>电话：{{ item.phone || '暂无' }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card-actions">
          <el-button size="small" type="text" @click="handleEdit(item)">
            <i class="el-icon-edit"></i>
            编辑
          </el-button>
          <el-button size="small" type="text" class="help-btn">
            <i class="el-icon-phone"></i>
            联系救助
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-state" v-if="tableData.length === 0">
      <div class="empty-icon">
        <i class="el-icon-warning-outline"></i>
      </div>
      <h3>暂无救助信息</h3>
      <p>成为第一个发布救助信息的爱心人士</p>
      <el-button type="primary" @click="handleAdd" class="empty-action-btn">
        <i class="el-icon-plus"></i>
        发布救助信息
      </el-button>
    </div>

    <!-- 分页器 -->
    <div class="pagination-wrapper" v-if="tableData.length > 0">
      <el-pagination 
        @size-change="handleSizeChange" 
        @current-change="handleCurrentChange"
        :current-page="pageNum" 
        :page-sizes="[5, 10, 20, 50]" 
        :page-size="pageSize" 
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background>
      </el-pagination>
    </div>


    <div style="margin: 10px 0">
      <div class="rescueFind">
        <i class="el-icon-reading" style="margin-right: 5px"/>
        宠物援助站
      </div>
      <!-- 救助站模块-->
      <rescue></rescue>
    </div>

    <div style="margin: 10px 0">
      <div class="feed"> 宠物喂养点</div>
      <!-- 救助站模块-->
      <feed></feed>
    </div>

    <el-dialog 
      title="新增救助信息" 
      :visible.sync="dialogFormVisible" 
      width="600px" 
      :close-on-click-modal="false"
      class="salvation-dialog"
      center>
      <div class="dialog-header">
        <i class="el-icon-first-aid-kit"></i>
        <span>请填写详细的救助信息，让更多爱心人士可以帮助到小动物</span>
      </div>
      
      <el-form 
        :model="form" 
        label-width="120px" 
        class="salvation-form"
        label-position="left">
        
        <el-form-item label="情况描述" required>
          <el-input 
            v-model="form.information" 
            type="textarea"
            :rows="3"
            placeholder="请详细描述动物的具体情况，如受伤程度、外观特征等"
            show-word-limit
            maxlength="200"
            autocomplete="off">
          </el-input>
        </el-form-item>
        
        <el-form-item label="现场照片" required>
          <div class="upload-section">
            <el-upload 
              :action="$store.state.baseApi + '/file/upload'" 
              ref="img" 
              :on-success="handleImgUploadSuccess"
              :before-upload="beforeUpload"
              list-type="picture-card"
              :limit="1"
              accept="image/*">
              <i class="el-icon-plus upload-icon"></i>
            </el-upload>
            <div class="upload-tip">
              <i class="el-icon-info"></i>
              请上传清晰的现场照片，支持 JPG、PNG 格式，大小不超过 5MB
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="发现地点" required>
          <el-input 
            v-model="form.address" 
            placeholder="请填写具体的地址信息，如：北京市朝阳区XX路 XX号"
            prefix-icon="el-icon-location"
            autocomplete="off">
          </el-input>
        </el-form-item>
        
        <el-form-item label="发现时间" required>
          <el-date-picker 
            v-model="form.time" 
            type="datetime" 
            value-format="yyyy-MM-dd HH:mm:ss"
            format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择发现时间"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        
        <div class="contact-section">
          <h4 class="section-title">
            <i class="el-icon-phone"></i>
            联系方式
          </h4>
          
          <el-form-item label="联系人" required>
            <el-input 
              v-model="form.person" 
              placeholder="请填写真实姓名或昵称"
              prefix-icon="el-icon-user"
              autocomplete="off">
            </el-input>
          </el-form-item>
          
          <el-form-item label="联系电话" required>
            <el-input 
              v-model="form.phone" 
              placeholder="请填写手机号码或固定电话"
              prefix-icon="el-icon-phone"
              autocomplete="off">
            </el-input>
          </el-form-item>
        </div>
        
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="medium" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="save" size="medium" class="confirm-btn">
          <i class="el-icon-check"></i>
          确定提交
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import rescue from '@/components/front/Rescue.vue'
import feed from '@/components/front/Feed.vue'
export default {
  name: "Salvation",
  components: {
    rescue,
    feed
  },
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 5,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/salvation/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.request.post("/salvation", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.$nextTick(() => {
        if (this.$refs.img) {
          this.$refs.img.clearFiles();
        }
        if (this.$refs.file) {
          this.$refs.file.clearFiles();
        }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
      this.$nextTick(() => {
        if (this.$refs.img) {
          this.$refs.img.clearFiles();
        }
        if (this.$refs.file) {
          this.$refs.file.clearFiles();
        }
      })
    },
    formatTime(time) {
      if (!time) return '未知时间'
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) {
        const hours = Math.floor(diff / (1000 * 60 * 60))
        if (hours === 0) {
          const minutes = Math.floor(diff / (1000 * 60))
          return minutes <= 0 ? '刚刚' : `${minutes}分钟前`
        }
        return `${hours}小时前`
      } else if (days === 1) {
        return '昨天'
      } else if (days < 7) {
        return `${days}天前`
      } else {
        return date.toLocaleDateString('zh-CN')
      }
    },
    del(id) {
      this.request.delete("/salvation/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/salvation/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
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
    handleFileUploadSuccess(res) {
      this.form.file = res
    },
    handleImgUploadSuccess(res) {
      this.form.img = res
    },
    beforeUpload(file) {
      const isImage = file.type.indexOf('image/') !== -1
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('上传文件只能是图片格式!')
        return false
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!')
        return false
      }
      return true
    },
    download(url) {
      window.open(url)
    },
    exp() {
      window.open(this.$store.state.baseApi + "/salvation/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style scoped>
.salvation-container {
  min-height: calc(100vh - 70px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

/* 页面头部样式 */
.salvation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.header-content .page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-content .page-title i {
  font-size: 36px;
  color: #FFD700;
}

.header-content .page-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.6;
}

.add-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  border: none;
  border-radius: 50px;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.6);
}

/* 救助信息卡片样式 */
.rescue-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.card-item {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.card-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px 15px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.status-badge.resolved {
  background: rgba(76, 175, 80, 0.2);
}

.status-badge.pending {
  background: rgba(255, 193, 7, 0.2);
}

.rescue-time {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  opacity: 0.9;
}

.card-content {
  display: flex;
  padding: 25px;
  gap: 20px;
}

.image-section {
  flex-shrink: 0;
}

.rescue-image {
  width: 120px;
  height: 120px;
  border-radius: 15px;
  overflow: hidden;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  color: #999;
  font-size: 30px;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.rescue-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.rescue-title i {
  color: #e74c3c;
}

.rescue-description {
  color: #7f8c8d;
  line-height: 1.6;
  margin: 0;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.rescue-description i {
  color: #3498db;
  margin-top: 2px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #5a6c7d;
}

.contact-item i {
  color: #9b59b6;
  width: 16px;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 0 25px 25px;
}

.help-btn {
  color: #27ae60 !important;
}

.help-btn:hover {
  background: rgba(39, 174, 96, 0.1) !important;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 80px;
  color: #bdc3c7;
  margin-bottom: 20px;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-state p {
  color: #7f8c8d;
  margin-bottom: 30px;
}

.empty-action-btn {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  border: none;
  border-radius: 50px;
  padding: 15px 40px;
  font-size: 16px;
  font-weight: 600;
}

/* 分页器样式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 30px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* 模块标题样式 */
.rescueFind {
  margin-top: 40px;
  padding: 25px 30px;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  color: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(116, 185, 255, 0.3);
  display: flex;
  align-items: center;
  gap: 15px;
}

.rescueFind i {
  font-size: 32px;
  color: #FFD700;
}

.feed {
  margin-top: 40px;
  padding: 25px 30px;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  color: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 184, 148, 0.3);
  display: flex;
  align-items: center;
  gap: 15px;
}

.feed::before {
  content: '🍽️';
  font-size: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .salvation-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .rescue-cards {
    grid-template-columns: 1fr;
  }
  
  .card-content {
    flex-direction: column;
  }
  
  .header-content .page-title {
    font-size: 24px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-item {
  animation: fadeInUp 0.6s ease forwards;
}

.card-item:nth-child(even) {
  animation-delay: 0.1s;
}

.card-item:nth-child(odd) {
  animation-delay: 0.2s;
}

/* 对话框样式 */
.salvation-dialog .el-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.salvation-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 25px 30px;
}

.salvation-dialog .el-dialog__title {
  font-size: 20px;
  font-weight: 600;
  color: white;
}

.salvation-dialog .el-dialog__body {
  padding: 30px;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 25px;
  padding: 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 15px;
  color: white;
  font-size: 16px;
  line-height: 1.5;
}

.dialog-header i {
  font-size: 24px;
  color: #FFD700;
}

.salvation-form {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 15px;
  border: 1px solid #e9ecef;
}

.salvation-form .el-form-item {
  margin-bottom: 25px;
}

.salvation-form .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

.salvation-form .el-input__inner,
.salvation-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.salvation-form .el-input__inner:focus,
.salvation-form .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(52, 152, 219, 0.1);
  border: 1px solid rgba(52, 152, 219, 0.2);
  border-radius: 8px;
  color: #3498db;
  font-size: 14px;
  line-height: 1.4;
}

.upload-icon {
  font-size: 28px !important;
  color: #8c9eff !important;
}

.contact-section {
  margin-top: 30px;
  padding: 25px;
  background: white;
  border-radius: 15px;
  border: 2px dashed #e9ecef;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.section-title i {
  color: #9b59b6;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 25px 30px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

.cancel-btn {
  padding: 12px 30px;
  border-radius: 25px;
  border: 2px solid #e74c3c;
  color: #e74c3c;
  background: white;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: #e74c3c;
  color: white;
  transform: translateY(-2px);
}

.confirm-btn {
  padding: 12px 30px;
  border-radius: 25px;
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  border: none;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.3);
  transition: all 0.3s ease;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}
</style>
