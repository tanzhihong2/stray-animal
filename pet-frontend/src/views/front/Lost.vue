<template>
  <div class="lost-pets-container">
    <!-- 页面头部区域 -->
    <div class="lost-pets-header">
      <div class="header-content">
        <div class="header-icon">
          <i class="el-icon-search"></i>
        </div>
        <div class="header-text">
          <h1 class="page-title">寻找伴侣，回家之路</h1>
          <p class="page-subtitle">每一只走失的宠物都在等待与主人的重逢，让我们一起帮助它们找到回家的路</p>
        </div>
      </div>
      
      <!-- 统计信息 -->
      <div class="stats-section">
        <div class="stat-item">
          <i class="el-icon-warning-outline"></i>
          <div class="stat-content">
            <span class="stat-number">{{ getLostCount() }}</span>
            <span class="stat-label">待寻找</span>
          </div>
        </div>
        <div class="stat-item">
          <i class="el-icon-circle-check"></i>
          <div class="stat-content">
            <span class="stat-number">{{ getFoundCount() }}</span>
            <span class="stat-label">已找回</span>
          </div>
        </div>
        <div class="stat-item">
          <i class="el-icon-data-line"></i>
          <div class="stat-content">
            <span class="stat-number">{{ total }}</span>
            <span class="stat-label">总记录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <div class="search-filters">
        <div class="search-input">
          <el-input 
            v-model="searchKeyword"
            placeholder="请输入宠物名字、联系人或其他关键词"
            prefix-icon="el-icon-search"
            clearable
            @keyup.enter.native="handleSearch"
            class="search-field">
          </el-input>
          <el-button 
            type="primary" 
            @click="handleSearch" 
            class="search-btn">
            <i class="el-icon-search"></i>
            搜索
          </el-button>
        </div>
        
        <div class="filter-options">
          <el-select 
            v-model="filterType" 
            placeholder="宠物类型" 
            clearable 
            class="filter-select">
            <el-option label="全部" value=""></el-option>
            <el-option label="狗" value="狗"></el-option>
            <el-option label="猫" value="猫"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
          
          <el-select 
            v-model="filterStatus" 
            placeholder="找回状态" 
            clearable 
            class="filter-select">
            <el-option label="全部" value=""></el-option>
            <el-option label="未找到" value="未找到"></el-option>
            <el-option label="已找到" value="已找到"></el-option>
          </el-select>
          
          <el-button 
            @click="resetFilters" 
            class="reset-btn">
            <i class="el-icon-refresh"></i>
            重置
          </el-button>
          
          <!-- 新增丢失宠物按钮 -->
          <el-button 
            type="primary" 
            @click="handleAdd" 
            class="add-pet-btn"
            icon="el-icon-plus">
            <i class="el-icon-plus"></i>
            新增丢失宠物
          </el-button>
        </div>
      </div>
    </div>

    <!-- 丢失宠物卡片展示区域 -->
    <div class="pets-display" v-if="filteredData.length > 0">
      <div class="pets-grid">
        <div 
          class="pet-card" 
          v-for="(pet, index) in filteredData" 
          :key="index"
          :class="getPetCardClass(pet)">
          
          <!-- 卡片头部 -->
          <div class="pet-card-header">
            <div class="pet-info-main">
              <div class="pet-avatar">
                <i :class="getPetIcon(pet.type)"></i>
              </div>
              <div class="pet-basic-info">
                <h3 class="pet-name">{{ pet.nickname || '未知名字' }}</h3>
                <div class="pet-details">
                  <span class="pet-type">
                    <i class="el-icon-collection-tag"></i>
                    {{ pet.type || '未知品种' }}
                  </span>
                  <span class="pet-sex" :class="pet.sex === '公' ? 'male' : 'female'">
                    <i :class="pet.sex === '公' ? 'el-icon-male' : 'el-icon-female'"></i>
                    {{ pet.sex || '未知' }}
                  </span>
                </div>
              </div>
            </div>
            
            <!-- 状态标签 -->
            <div class="status-badges">
              <div class="status-badge primary-status" :class="getStatusClass(pet.status1)">
                <i :class="getStatusIcon(pet.status1)"></i>
                {{ pet.status1 || '未知状态' }}
              </div>
              <div class="status-badge secondary-status" :class="getFoundStatusClass(pet.status2)">
                <i :class="getFoundStatusIcon(pet.status2)"></i>
                {{ pet.status2 || '未知' }}
              </div>
            </div>
          </div>
          
          <!-- 卡片内容 -->
          <div class="pet-card-content">
            <!-- 联系信息 -->
            <div class="contact-section">
              <div class="contact-item">
                <i class="el-icon-user"></i>
                <div class="contact-details">
                  <span class="contact-label">联系人</span>
                  <span class="contact-value">{{ pet.person || '未填写' }}</span>
                </div>
              </div>
              
              <div class="contact-item">
                <i class="el-icon-phone"></i>
                <div class="contact-details">
                  <span class="contact-label">联系电话</span>
                  <span class="contact-value">{{ formatPhone(pet.phone) }}</span>
                </div>
              </div>
            </div>
            
            <!-- 时间信息 -->
            <div class="time-info" v-if="pet.lostTime">
              <i class="el-icon-time"></i>
              <span>丢失时间：{{ formatTime(pet.lostTime) }}</span>
            </div>
            
            <!-- 宠物描述 -->
            <div class="pet-description" v-if="pet.description">
              <i class="el-icon-document"></i>
              <p>{{ pet.description }}</p>
            </div>
          </div>
          
          <!-- 卡片操作 -->
          <div class="pet-card-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="contactOwner(pet)"
              class="contact-btn"
              :disabled="!pet.phone">
              <i class="el-icon-phone"></i>
              联系主人
            </el-button>
            
            <el-button 
              type="text" 
              size="small" 
              @click="shareInfo(pet)"
              class="share-btn">
              <i class="el-icon-share"></i>
              分享信息
            </el-button>
            
            <el-button 
              type="text" 
              size="small" 
              @click="reportFound(pet)"
              class="report-btn"
              v-if="pet.status2 === '未找到'">
              <i class="el-icon-star-on"></i>
              发现线索
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-state" v-if="filteredData.length === 0">
      <div class="empty-icon">
        <i class="el-icon-search"></i>
      </div>
      <h3>暂无相关信息</h3>
      <p v-if="hasActiveFilters">请尝试调整搜索条件或筛选条件</p>
      <p v-else>暂无丢失宠物信息，希望所有的宠物都能平安在家</p>
      <el-button 
        @click="resetFilters" 
        type="primary" 
        class="empty-action-btn"
        v-if="hasActiveFilters">
        <i class="el-icon-refresh"></i>
        清除筛选
      </el-button>
    </div>

    <!-- 分页器 -->
    <div class="pagination-wrapper" v-if="filteredData.length > 0">
      <el-pagination 
        @size-change="handleSizeChange" 
        @current-change="handleCurrentChange"
        :current-page="pageNum" 
        :page-sizes="[6, 12, 24, 48]" 
        :page-size="pageSize" 
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background>
      </el-pagination>
    </div>

    <el-dialog 
      title="发布丢失宠物信息" 
      :visible.sync="dialogFormVisible" 
      width="800px" 
      :close-on-click-modal="false"
      class="lost-pet-dialog"
      center>
      
      <div class="dialog-header">
        <i class="el-icon-warning-outline"></i>
        <span>请详细填写丢失宠物的相关信息，让更多爱心人士可以帮助寻找您的宠物</span>
      </div>
      
      <el-form 
        :model="form" 
        :rules="formRules"
        ref="lostForm"
        label-width="120px" 
        class="lost-pet-form"
        label-position="left">
        
        <div class="form-section">
          <h4 class="section-title">
            <i class="el-icon-star-on"></i>
            宠物信息
          </h4>
          
          <el-form-item label="宠物名字" prop="nickname">
            <el-input 
              v-model="form.nickname" 
              placeholder="请输入宠物的名字或昵称"
              prefix-icon="el-icon-user"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
          
          <el-form-item label="宠物种类" prop="type">
            <el-select 
              v-model="form.type" 
              placeholder="请选择宠物类型"
              style="width: 100%"
              clearable>
              <el-option label="狗" value="狗">
                <i class="el-icon-user" style="margin-right: 8px;"></i>
                狗
              </el-option>
              <el-option label="猫" value="猫">
                <i class="el-icon-star-on" style="margin-right: 8px;"></i>
                猫
              </el-option>
              <el-option label="其他" value="其他">
                <i class="el-icon-question" style="margin-right: 8px;"></i>
                其他
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="form.sex">
              <el-radio label="公" class="gender-radio male">
                <i class="el-icon-male"></i>
                公
              </el-radio>
              <el-radio label="母" class="gender-radio female">
                <i class="el-icon-female"></i>
                母
              </el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="品种描述">
            <el-input 
              v-model="form.breed" 
              placeholder="如：金毛、英短、中华田园犬等"
              prefix-icon="el-icon-collection-tag"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
          
          <el-form-item label="颜色特征">
            <el-input 
              v-model="form.color" 
              placeholder="请描述宠物的颜色特征"
              prefix-icon="el-icon-picture-outline"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
          
          <el-form-item label="年龄/大小">
            <el-input 
              v-model="form.age" 
              placeholder="如：3岁、幼犬、中型猫等"
              prefix-icon="el-icon-time"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
        </div>
        
        <div class="form-section">
          <h4 class="section-title">
            <i class="el-icon-location-outline"></i>
            丢失信息
          </h4>
          
          <el-form-item label="丢失时间">
            <el-date-picker
              v-model="form.lostTime"
              type="datetime"
              placeholder="选择丢失时间"
              style="width: 100%"
              value-format="yyyy-MM-dd HH:mm:ss">
            </el-date-picker>
          </el-form-item>
          
          <el-form-item label="丢失地点">
            <el-input 
              v-model="form.lostLocation" 
              placeholder="请详细描述丢失地点，如：XX小区、XX公园附近等"
              prefix-icon="el-icon-location"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
          
          <el-form-item label="详细描述">
            <el-input 
              type="textarea" 
              v-model="form.description"
              :rows="4"
              placeholder="请描述宠物的外观特征、性格、习性等，越详细越有利于寻找"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
        </div>
        
        <div class="form-section">
          <h4 class="section-title">
            <i class="el-icon-phone"></i>
            联系方式
          </h4>
          
          <el-form-item label="联系人" prop="person">
            <el-input 
              v-model="form.person" 
              placeholder="请填写真实姓名或昵称"
              prefix-icon="el-icon-user"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
          
          <el-form-item label="联系电话" prop="phone">
            <el-input 
              v-model="form.phone" 
              placeholder="请填写手机号码，方便好心人联系您"
              prefix-icon="el-icon-phone"
              autocomplete="off"
              clearable
              maxlength="11">
            </el-input>
          </el-form-item>
          
          <el-form-item label="微信号">
            <el-input 
              v-model="form.wechat" 
              placeholder="可选填，提供更多联系方式"
              prefix-icon="el-icon-chat-line-round"
              autocomplete="off"
              clearable>
            </el-input>
          </el-form-item>
        </div>
        
        <div class="form-section">
          <h4 class="section-title">
            <i class="el-icon-warning-outline"></i>
            状态信息
          </h4>
          
          <el-form-item label="当前状态" prop="status1">
            <el-radio-group v-model="form.status1">
              <el-radio label="已丢失" class="status-radio lost">
                <i class="el-icon-warning-outline"></i>
                已丢失
              </el-radio>
              <el-radio label="待领回" class="status-radio found">
                <i class="el-icon-circle-check"></i>
                待领回
              </el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="找回状态" prop="status2">
            <el-radio-group v-model="form.status2">
              <el-radio label="未找到" class="found-radio searching">
                <i class="el-icon-search"></i>
                未找到
              </el-radio>
              <el-radio label="已找到" class="found-radio success">
                <i class="el-icon-success"></i>
                已找到
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelAdd" size="medium" class="cancel-btn">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="save" size="medium" class="confirm-btn" :loading="saveLoading">
          <i class="el-icon-check"></i>
          发布信息
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Lost",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 12,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      // 新增的搜索和筛选字段
      searchKeyword: '',
      filterType: '',
      filterStatus: '',
      // 新增的加载状态
      saveLoading: false,
      // 表单验证规则
      formRules: {
        nickname: [
          { required: true, message: '请输入宠物名字', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择宠物类型', trigger: 'change' }
        ],
        sex: [
          { required: true, message: '请选择宠物性别', trigger: 'change' }
        ],
        person: [
          { required: true, message: '请输入联系人', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
        ],
        status1: [
          { required: true, message: '请选择当前状态', trigger: 'change' }
        ],
        status2: [
          { required: true, message: '请选择找回状态', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    filteredData() {
      let data = this.tableData
      
      // 关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        data = data.filter(item => 
          (item.nickname && item.nickname.toLowerCase().includes(keyword)) ||
          (item.person && item.person.toLowerCase().includes(keyword)) ||
          (item.phone && item.phone.includes(keyword))
        )
      }
      
      // 类型筛选
      if (this.filterType) {
        data = data.filter(item => item.type === this.filterType)
      }
      
      // 状态筛选
      if (this.filterStatus) {
        data = data.filter(item => item.status2 === this.filterStatus)
      }
      
      return data
    },
    hasActiveFilters() {
      return this.searchKeyword || this.filterType || this.filterStatus
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/lost/page", {
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
      // 表单验证
      this.$refs.lostForm.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          
          // 添加当前时间戳
          if (!this.form.lostTime) {
            this.form.lostTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
          }
          
          this.request.post("/lost", this.form).then(res => {
            this.saveLoading = false
            if (res.code === '200') {
              this.$message({
                message: '丢失宠物信息发布成功！希望尽快找到您的宠物。',
                type: 'success',
                duration: 3000
              })
              this.dialogFormVisible = false
              this.resetForm()
              this.load() // 重新加载列表
            } else {
              this.$message.error(res.msg || '发布失败，请稍后再试')
            }
          }).catch(error => {
            this.saveLoading = false
            console.error('发布错误:', error)
            this.$message.error('网络错误，请检查网络连接')
          })
        } else {
          this.$message.warning('请正确填写所有必填信息')
          return false
        }
      })
    },
    // 新增的方法
    getLostCount() {
      return this.tableData.filter(item => item.status2 === '未找到').length
    },
    getFoundCount() {
      return this.tableData.filter(item => item.status2 === '已找到').length
    },
    getPetIcon(type) {
      const icons = {
        '狗': 'el-icon-user',
        '猫': 'el-icon-star-on',
        '其他': 'el-icon-question'
      }
      return icons[type] || 'el-icon-question'
    },
    getPetCardClass(pet) {
      return {
        'lost-status': pet.status1 === '已丢失',
        'found-status': pet.status1 === '待领回',
        'resolved': pet.status2 === '已找到'
      }
    },
    getStatusClass(status) {
      return {
        'lost': status === '已丢失',
        'waiting': status === '待领回'
      }
    },
    getStatusIcon(status) {
      return status === '已丢失' ? 'el-icon-warning-outline' : 'el-icon-circle-check'
    },
    getFoundStatusClass(status) {
      return {
        'not-found': status === '未找到',
        'found': status === '已找到'
      }
    },
    getFoundStatusIcon(status) {
      return status === '已找到' ? 'el-icon-success' : 'el-icon-search'
    },
    formatPhone(phone) {
      if (!phone) return '未填写'
      return phone.length === 11 ? 
        phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3') : 
        phone
    },
    formatTime(time) {
      if (!time) return '未知时间'
      const date = new Date(time)
      return date.toLocaleDateString('zh-CN')
    },
    handleSearch() {
      // 重新加载数据或直接使用计算属性的筛选结果
      this.pageNum = 1 // 重置到第一页
    },
    resetFilters() {
      this.searchKeyword = ''
      this.filterType = ''
      this.filterStatus = ''
      this.pageNum = 1
    },
    contactOwner(pet) {
      if (pet.phone) {
        this.$confirm(`联系 ${pet.person || '宠物主人'}：${pet.phone}`, '联系宠物主人', {
          confirmButtonText: '拨打电话',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          window.open(`tel:${pet.phone}`)
        })
      } else {
        this.$message.warning('该记录暂无联系电话')
      }
    },
    shareInfo(pet) {
      const shareText = `寻找丢失宠物：${pet.nickname || '未知名字'}（${pet.type || '未知品种'}），联系人：${pet.person || '未填写'}，电话：${pet.phone || '未填写'}`
      
      if (navigator.share) {
        navigator.share({
          title: '寻找丢失宠物',
          text: shareText
        })
      } else {
        // 复制到剪贴板
        navigator.clipboard.writeText(shareText).then(() => {
          this.$message.success('信息已复制到剪贴板')
        })
      }
    },
    reportFound(pet) {
      this.$confirm('您确定发现了这只宠物吗？', '发现线索', {
        confirmButtonText: '确定发现',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('感谢您的反馈，我们将尽快联系宠物主人！')
      })
    },
    handleAdd() {
      if (!this.user.id) {
        this.$message.warning('请先登录后再发布丢失宠物信息')
        this.$router.push('/login')
        return
      }
      
      this.dialogFormVisible = true
      this.resetForm()
      this.$nextTick(() => {
        if (this.$refs.lostForm) {
          this.$refs.lostForm.clearValidate()
        }
      })
    },
    
    // 重置表单方法
    resetForm() {
      this.form = {
        nickname: '',
        type: '',
        sex: '',
        breed: '',
        color: '',
        age: '',
        lostTime: '',
        lostLocation: '',
        description: '',
        person: '',
        phone: '',
        wechat: '',
        status1: '已丢失', // 默认状态
        status2: '未找到'  // 默认状态
      }
      this.saveLoading = false
    },
    
    // 取消新增
    cancelAdd() {
      this.$confirm('确定要取消发布吗？已填写的信息将不会保存。', '提示', {
        confirmButtonText: '确定取消',
        cancelButtonText: '继续编辑',
        type: 'warning'
      }).then(() => {
        this.dialogFormVisible = false
        this.resetForm()
      }).catch(() => {
        // 用户取消操作，继续编辑
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
       this.$nextTick(() => {
         if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
       })
    },
    del(id) {
      this.request.delete("/lost/" + id).then(res => {
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
      this.request.post("/lost/del/batch", ids).then(res => {
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
    download(url) {
      window.open(url)
    },
    exp() {
      window.open(this.$store.state.baseApi+"/lost/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style scoped>
.lost-pets-container {
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

/* 页面头部样式 */
.lost-pets-header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  border-radius: 25px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 15px 35px rgba(255, 154, 158, 0.3);
  color: white;
  position: relative;
  overflow: hidden;
}

.lost-pets-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: floating 6s ease-in-out infinite;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 25px;
  margin-bottom: 30px;
}

.header-icon {
  background: rgba(255, 255, 255, 0.2);
  padding: 20px;
  border-radius: 50%;
  font-size: 40px;
  backdrop-filter: blur(10px);
}

.header-text .page-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 15px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-text .page-subtitle {
  font-size: 16px;
  opacity: 0.95;
  margin: 0;
  line-height: 1.6;
  max-width: 600px;
}

/* 统计信息样式 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.35);
}

.stat-item i {
  font-size: 28px;
  color: #FFD700;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 5px;
}

/* 搜索区域样式 */
.search-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.search-filters {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-input {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-field {
  flex: 1;
}

.search-field .el-input__inner {
  border-radius: 25px;
  border: 2px solid #e9ecef;
  padding: 12px 20px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.search-field .el-input__inner:focus {
  border-color: #ff9a9e;
  box-shadow: 0 0 0 3px rgba(255, 154, 158, 0.1);
}

.search-btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 30px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 154, 158, 0.4);
}

.filter-options {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  min-width: 150px;
}

.filter-select .el-input__inner {
  border-radius: 15px;
  border: 2px solid #e9ecef;
}

.reset-btn {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 15px;
  color: #6c757d;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background: #e9ecef;
  border-color: #dee2e6;
}

/* 新增宠物按钮样式 */
.add-pet-btn {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
  border: none;
  border-radius: 15px;
  color: white;
  font-weight: 600;
  padding: 12px 25px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(108, 92, 231, 0.3);
}

.add-pet-btn:hover {
  background: linear-gradient(135deg, #5f4fcf 0%, #9085e8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(108, 92, 231, 0.4);
  color: white;
}

/* 头部快捷操作按钮 */
.quick-action {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px) !important;
  border: 2px solid rgba(255, 255, 255, 0.2) !important;
  padding: 15px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.quick-action:hover {
  background: rgba(255, 255, 255, 0.25) !important;
  transform: none !important;
}

.quick-add-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa8a8 100%) !important;
  border: none !important;
  border-radius: 20px !important;
  color: white !important;
  font-weight: 600 !important;
  padding: 12px 20px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3) !important;
}

.quick-add-btn:hover {
  background: linear-gradient(135deg, #ff5252 0%, #ff8a80 100%) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4) !important;
  color: white !important;
}

/* 宠物卡片展示区域 */
.pets-display {
  margin-bottom: 30px;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 25px;
}

.pet-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
}

.pet-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.pet-card.lost-status {
  border-color: #ff6b6b;
}

.pet-card.found-status {
  border-color: #51cf66;
}

.pet-card.resolved {
  opacity: 0.8;
  border-color: #95a5a6;
}

/* 卡片头部 */
.pet-card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.pet-info-main {
  display: flex;
  gap: 15px;
  align-items: center;
  flex: 1;
}

.pet-avatar {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  backdrop-filter: blur(10px);
}

.pet-basic-info {
  flex: 1;
}

.pet-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.pet-details {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.pet-type, .pet-sex {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 12px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
}

.pet-sex.male {
  color: #74b9ff;
}

.pet-sex.female {
  color: #fd79a8;
}

/* 状态标签 */
.status-badges {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.status-badge.lost {
  background: rgba(255, 107, 107, 0.9);
}

.status-badge.waiting {
  background: rgba(81, 207, 102, 0.9);
}

.status-badge.not-found {
  background: rgba(255, 193, 7, 0.9);
}

.status-badge.found {
  background: rgba(40, 167, 69, 0.9);
}

/* 卡片内容 */
.pet-card-content {
  padding: 25px;
}

.contact-section {
  margin-bottom: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.contact-item i {
  color: #667eea;
  font-size: 18px;
  width: 20px;
  flex-shrink: 0;
}

.contact-details {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.contact-label {
  font-size: 12px;
  color: #6c757d;
  margin-bottom: 2px;
}

.contact-value {
  font-weight: 600;
  color: #2c3e50;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 15px;
}

.time-info i {
  color: #ffc107;
}

.pet-description {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #6c757d;
  line-height: 1.5;
}

.pet-description i {
  color: #17a2b8;
  margin-top: 2px;
}

.pet-description p {
  margin: 0;
  flex: 1;
}

/* 卡片操作 */
.pet-card-actions {
  display: flex;
  justify-content: space-between;
  padding: 0 25px 25px;
  gap: 10px;
}

.contact-btn {
  background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
  border: none;
  border-radius: 20px;
  font-weight: 600;
  flex: 1;
  transition: all 0.3s ease;
}

.contact-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

.contact-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.share-btn {
  color: #9b59b6 !important;
  font-weight: 600;
  border-radius: 20px;
}

.share-btn:hover {
  background: rgba(155, 89, 182, 0.1) !important;
}

.report-btn {
  color: #f39c12 !important;
  font-weight: 600;
  border-radius: 20px;
}

.report-btn:hover {
  background: rgba(243, 156, 18, 0.1) !important;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.empty-state .empty-icon {
  font-size: 80px;
  color: #bdc3c7;
  margin-bottom: 20px;
}

.empty-state h3 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 20px;
}

.empty-state p {
  color: #7f8c8d;
  margin-bottom: 25px;
  line-height: 1.6;
}

.empty-action-btn {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  border: none;
  border-radius: 25px;
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 30px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* 对话框样式 */
.lost-pet-dialog .el-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.lost-pet-dialog .el-dialog__header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: white;
  padding: 25px 30px;
}

.lost-pet-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
  font-size: 20px;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 25px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  color: white;
  font-size: 16px;
  line-height: 1.5;
}

.dialog-header i {
  font-size: 24px;
  color: #FFD700;
}

.lost-pet-form {
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
  color: #ff9a9e;
}

.lost-pet-form .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

.lost-pet-form .el-input__inner,
.lost-pet-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.lost-pet-form .el-input__inner:focus,
.lost-pet-form .el-textarea__inner:focus {
  border-color: #ff9a9e;
  box-shadow: 0 0 0 2px rgba(255, 154, 158, 0.1);
}

/* 单选按钮样式 */
.gender-radio.male .el-radio__input.is-checked .el-radio__inner {
  background-color: #74b9ff;
  border-color: #74b9ff;
}

.gender-radio.female .el-radio__input.is-checked .el-radio__inner {
  background-color: #fd79a8;
  border-color: #fd79a8;
}

.status-radio.lost .el-radio__input.is-checked .el-radio__inner {
  background-color: #ff6b6b;
  border-color: #ff6b6b;
}

.status-radio.found .el-radio__input.is-checked .el-radio__inner {
  background-color: #51cf66;
  border-color: #51cf66;
}

.found-radio.searching .el-radio__input.is-checked .el-radio__inner {
  background-color: #ffc107;
  border-color: #ffc107;
}

.found-radio.success .el-radio__input.is-checked .el-radio__inner {
  background-color: #28a745;
  border-color: #28a745;
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
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border: none;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
  transition: all 0.3s ease;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .lost-pets-header {
    padding: 25px 20px;
  }
  
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .header-text .page-title {
    font-size: 28px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .search-input {
    flex-direction: column;
  }
  
  .filter-options {
    justify-content: center;
  }
  
  .pets-grid {
    grid-template-columns: 1fr;
  }
  
  .pet-card-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .pet-card-actions {
    flex-direction: column;
  }
}

/* 动画效果 */
@keyframes floating {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

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

.pet-card {
  animation: slideInUp 0.6s ease forwards;
}

.pet-card:nth-child(even) {
  animation-delay: 0.1s;
}

.pet-card:nth-child(odd) {
  animation-delay: 0.2s;
}

/* 古典样式兼容 */
.headerBg {
  background: #eee !important;
}

.lost {
  padding: 20px 10px;
  font-size: 28px;
  background-color: #fff;
  border-radius: 10px;
  color: red;
}
</style>
