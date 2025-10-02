<template>
  <div class="feed-module">
    <!-- 喂养点卡片列表 -->
    <div class="feed-cards" v-if="tableData.length > 0">
      <div class="feed-card" v-for="(item, index) in tableData" :key="index">
        <div class="card-header">
          <div class="feed-badge">
            <i class="el-icon-food"></i>
            喂养点
          </div>
          <div class="feed-status">
            <i class="el-icon-circle-check"></i>
            正常运营
          </div>
        </div>
        
        <!-- 环境照片轮播 -->
        <div class="images-gallery">
          <div class="gallery-container">
            <div class="image-item" v-if="item.img">
              <el-image 
                class="feed-image"
                :src="$store.state.baseApi+item.img" 
                :preview-src-list="getImageList(item)"
                fit="cover">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span class="image-label">环境照片1</span>
            </div>
            
            <div class="image-item" v-if="item.img2">
              <el-image 
                class="feed-image"
                :src="$store.state.baseApi+item.img2" 
                :preview-src-list="getImageList(item)"
                fit="cover">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span class="image-label">环境照片2</span>
            </div>
            
            <div class="image-item" v-if="item.img3">
              <el-image 
                class="feed-image"
                :src="$store.state.baseApi+item.img3" 
                :preview-src-list="getImageList(item)"
                fit="cover">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span class="image-label">环境照片3</span>
            </div>
          </div>
        </div>
        
        <!-- 喂养点信息 -->
        <div class="card-content">
          <div class="feed-info">
            <div class="info-item main-info">
              <i class="el-icon-location-outline"></i>
              <div class="info-text">
                <span class="label">喂养点地址</span>
                <span class="value">{{ item.address || '地址信息待完善' }}</span>
              </div>
            </div>
            
            <div class="info-item" v-if="item.information">
              <i class="el-icon-document"></i>
              <div class="info-text">
                <span class="label">相关描述</span>
                <span class="value description-text">{{ item.information }}</span>
              </div>
            </div>
          </div>
          
          <!-- 喂养提示 -->
          <div class="feed-tips">
            <div class="tip-item">
              <i class="el-icon-time"></i>
              <span>每日 7:00-19:00 可投放食物</span>
            </div>
            <div class="tip-item">
              <i class="el-icon-warning-outline"></i>
              <span>请使用专用猫狗粮，禁止投放巧克力等有害食物</span>
            </div>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="card-actions">
          <el-button 
            type="primary" 
            size="small" 
            @click="getDirections(item)"
            class="direction-btn">
            <i class="el-icon-position"></i>
            获取路线
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="viewGallery(item)"
            class="gallery-btn">
            <i class="el-icon-picture"></i>
            查看图片
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="reportFeed(item)"
            class="report-btn">
            <i class="el-icon-message"></i>
            上报情况
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-feed" v-if="tableData.length === 0">
      <div class="empty-icon">
        <i class="el-icon-food"></i>
      </div>
      <h4>暂无喂养点信息</h4>
      <p>正在努力建设更多的喂养点，请稍后再来查看</p>
      <div class="volunteer-tip">
        <i class="el-icon-star-on"></i>
        <span>欢迎志愿者参与喂养点建设</span>
      </div>
    </div>

    <!-- 分页器 -->
    <div class="pagination-container" v-if="tableData.length > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[4, 8, 16]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total"
        background
        small>
      </el-pagination>
    </div>

    <!-- 喂养点管理对话框 -->
    <el-dialog 
      title="喂养点信息管理" 
      :visible.sync="dialogFormVisible" 
      width="600px" 
      :close-on-click-modal="false"
      class="feed-dialog">
      <el-form 
        :model="form" 
        label-width="120px" 
        class="feed-form">
        
        <div class="form-section">
          <h4 class="section-title">
            <i class="el-icon-picture"></i>
            环境照片上传
          </h4>
          
          <el-form-item label="环境照片1">
            <el-upload 
              :action="$store.state.baseApi+'/file/upload'" 
              ref="img1" 
              :on-success="handleImgUploadSuccess"
              list-type="picture-card"
              :limit="1">
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="环境照片2">
            <el-upload 
              :action="$store.state.baseApi+'/file/upload'" 
              ref="img2" 
              :on-success="handleImgUploadSuccess2"
              list-type="picture-card"
              :limit="1">
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="环境照片3">
            <el-upload 
              :action="$store.state.baseApi+'/file/upload'" 
              ref="img3" 
              :on-success="handleImgUploadSuccess3"
              list-type="picture-card"
              :limit="1">
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </div>
        
        <div class="form-section">
          <h4 class="section-title">
            <i class="el-icon-location"></i>
            喂养点详情
          </h4>
          
          <el-form-item label="详细地址">
            <el-input 
              v-model="form.address" 
              placeholder="请输入喂养点的详细地址"
              autocomplete="off">
            </el-input>
          </el-form-item>
          
          <el-form-item label="相关描述">
            <el-input 
              v-model="form.information" 
              type="textarea"
              :rows="4"
              placeholder="请描述喂养点的相关信息，如周边环境、注意事项等"
              autocomplete="off">
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="save" class="save-btn">
          <i class="el-icon-check"></i>
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Feed",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 4,
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
      this.request.get("/feed/page", {
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
      this.request.post("/feed", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    getImageList(item) {
      const images = []
      if (item.img) images.push(this.$store.state.baseApi + item.img)
      if (item.img2) images.push(this.$store.state.baseApi + item.img2)
      if (item.img3) images.push(this.$store.state.baseApi + item.img3)
      return images
    },
    getDirections(item) {
      if (item.address) {
        this.$confirm(`将跳转到地图应用获取路线\n目的地：${item.address}`, '获取路线', {
          confirmButtonText: '立即跳转',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          // 这里可以添加跳转到地图应用的逻辑
          window.open(`https://uri.amap.com/marker?position=116.481488,39.990464&name=${encodeURIComponent(item.address)}`)
        })
      } else {
        this.$message.warning('该喂养点暂无地址信息')
      }
    },
    viewGallery(item) {
      const images = this.getImageList(item)
      if (images.length > 0) {
        // 可以添加图片查看逻辑
        this.$message.info('点击图片即可查看大图')
      } else {
        this.$message.warning('该喂养点暂无图片')
      }
    },
    reportFeed(item) {
      this.$confirm('是否要上报该喂养点的情况？', '上报情况', {
        confirmButtonText: '上报',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('上报成功，感谢您的反馈！')
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.$nextTick(() => {
        if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
        if(this.$refs.img3) {
          this.$refs.img3.clearFiles();
        }
        if(this.$refs.img2) {
          this.$refs.img2.clearFiles();
        }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
       this.$nextTick(() => {
         if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.img3) {
          this.$refs.img3.clearFiles();
         }
         if(this.$refs.img2) {
           this.$refs.img2.clearFiles();
         }
       })
    },
    del(id) {
      this.request.delete("/feed/" + id).then(res => {
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
      this.request.post("/feed/del/batch", ids).then(res => {
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
    handleImgUploadSuccess2(res) {
      this.form.img2 = res
    },
    handleImgUploadSuccess3(res) {
      this.form.img3 = res
    },
    download(url) {
      window.open(url)
    },
    exp() {
      window.open(this.$store.state.baseApi+"/feed/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style scoped>
.feed-module {
  margin: 20px 0;
}

/* 喂养点卡片布局 */
.feed-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.feed-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.feed-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  color: white;
}

.feed-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.feed-badge i {
  font-size: 20px;
  color: #FFD700;
}

.feed-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 12px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
}

/* 图片画廊 */
.images-gallery {
  padding: 20px 25px 0;
}

.gallery-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
}

.image-item {
  position: relative;
}

.feed-image {
  width: 100%;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ddd6fe 0%, #c4b5fd 100%);
  color: #8b5cf6;
  font-size: 24px;
}

.image-label {
  position: absolute;
  bottom: 5px;
  left: 5px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

/* 卡片内容 */
.card-content {
  padding: 25px;
}

.feed-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 15px;
}

.info-item i {
  color: #00b894;
  font-size: 18px;
  margin-top: 2px;
  flex-shrink: 0;
}

.info-text {
  flex: 1;
}

.info-text .label {
  display: block;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
  font-size: 15px;
}

.info-text .value {
  color: #7f8c8d;
  line-height: 1.5;
}

.description-text {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.main-info {
  margin-bottom: 20px;
}

.main-info .info-text .label {
  font-size: 16px;
  color: #2c3e50;
}

.main-info .info-text .value {
  font-size: 15px;
  color: #34495e;
  font-weight: 500;
}

/* 喂养提示 */
.feed-tips {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.tip-item i {
  color: #e17055;
  margin-top: 2px;
  flex-shrink: 0;
}

.tip-item span {
  color: #2d3436;
  font-weight: 500;
}

/* 卡片操作 */
.card-actions {
  display: flex;
  justify-content: space-between;
  padding: 0 25px 25px;
  gap: 10px;
}

.direction-btn {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 600;
  color: white;
  flex: 1;
  transition: all 0.3s ease;
}

.direction-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(116, 185, 255, 0.4);
}

.gallery-btn {
  color: #9b59b6 !important;
  font-weight: 600;
  border-radius: 20px;
  padding: 10px 15px;
}

.gallery-btn:hover {
  background: rgba(155, 89, 182, 0.1) !important;
}

.report-btn {
  color: #e74c3c !important;
  font-weight: 600;
  border-radius: 20px;
  padding: 10px 15px;
}

.report-btn:hover {
  background: rgba(231, 76, 60, 0.1) !important;
}

/* 空状态 */
.empty-feed {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.empty-feed .empty-icon {
  font-size: 60px;
  color: #00b894;
  margin-bottom: 20px;
}

.empty-feed h4 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 18px;
}

.empty-feed p {
  color: #7f8c8d;
  margin-bottom: 20px;
}

.volunteer-tip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #fdcb6e 0%, #e84393 100%);
  color: white;
  padding: 12px 20px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 14px;
}

/* 分页器 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 25px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* 对话框样式 */
.feed-dialog .el-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.feed-dialog .el-dialog__header {
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  color: white;
  padding: 25px 30px;
}

.feed-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
}

.feed-form {
  padding: 20px;
}

.form-section {
  margin-bottom: 30px;
  padding: 25px;
  background: #f8f9fa;
  border-radius: 15px;
  border: 1px solid #e9ecef;
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
  color: #00b894;
}

.feed-form .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

.feed-form .el-input__inner,
.feed-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.feed-form .el-input__inner:focus,
.feed-form .el-textarea__inner:focus {
  border-color: #00b894;
  box-shadow: 0 0 0 2px rgba(0, 184, 148, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 25px;
}

.cancel-btn {
  padding: 10px 25px;
  border-radius: 20px;
  border: 2px solid #e74c3c;
  color: #e74c3c;
  background: white;
  font-weight: 600;
}

.cancel-btn:hover {
  background: #e74c3c;
  color: white;
}

.save-btn {
  padding: 10px 25px;
  border-radius: 20px;
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  border: none;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .feed-cards {
    grid-template-columns: 1fr;
  }
  
  .gallery-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .card-actions {
    flex-direction: column;
  }
}

/* 动画效果 */
@keyframes bounceInUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.feed-card {
  animation: bounceInUp 0.6s ease forwards;
}

.feed-card:nth-child(even) {
  animation-delay: 0.1s;
}

.feed-card:nth-child(odd) {
  animation-delay: 0.2s;
}
</style>
