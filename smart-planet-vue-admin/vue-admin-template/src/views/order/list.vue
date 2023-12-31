<template>
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <el-form :inline="true" class="demo-form-inline">
        <el-form-item>
          <el-input v-model="searchObj.outTradeNo" placeholder="订单号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchObj.phone" placeholder="手机" />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="searchObj.createTimeBegin"
            type="date"
            placeholder="选择下单开始日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="searchObj.createTimeEnd"
            type="date"
            placeholder="选择截止日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchObj.orderStatus"
            placeholder="订单状态"
            class="v-select patient-select"
          >
            <el-option
              v-for="item in statusList"
              :key="item.status"
              :label="item.name"
              :value="item.status"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="fetchData()"
          >查询</el-button
        >
        <el-button type="default" @click="resetData()">清空</el-button>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="outTradeNo" label="订单号" width="160" />
      <el-table-column prop="courseName" label="课程名称" width="160">
        <template slot-scope="scope">
          {{ scope.row.param.courseName }}
        </template>
      </el-table-column>
      <el-table-column prop="finalAmount" label="订单金额" width="90" />
      <el-table-column prop="nickName" label="下单用户" />
      <el-table-column prop="phone" label="用户手机" />
      <el-table-column prop="payTime" label="支付时间" width="156" />
      <el-table-column prop="orderStatus" label="订单状态">
        <template slot-scope="scope">
          {{ scope.row.orderStatus == 0 ? "未支付" : "已支付" }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="156" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      :page-sizes="[5, 10, 20, 30, 40, 50, 100]"
      style="padding: 30px 0; text-align: center"
      layout="sizes, prev, pager, next, jumper, ->, total, slot"
      @current-change="fetchData"
      @size-change="changeSize"
    />
  </div>
</template>

<script>
import orderInfoAPI from "@/api/order/orderInfo";

export default {
  data() {
    return {
      // 数据是否正在加载
      listLoading: true,

      // banner 列表
      list: null,

      // 数据库中的总记录数
      total: 0,

      // 默认页码
      page: 1,

      // 每页记录数
      limit: 10,

      // 查询表单对象
      searchObj: {},

      statusList: [
        {
          status: 0,
          name: "未支付",
        },
        {
          status: 1,
          name: "已支付",
        },
      ],
    };
  },

  // 生命周期函数：内存准备完毕，页面尚未渲染
  created() {
    this.fetchData();
  },

  // 生命周期函数：内存准备完毕，页面渲染成功
  mounted() {
    console.log("list mounted...");
  },

  methods: {
    // 当页码发生改变的时候
    changeSize(size) {
      this.limit = size;
      this.fetchData(1);
    },

    // 加载 banner 列表数据
    fetchData(page = 1) {
      // 验证开始时间和结束时间的合法性
      if (!this.validateDateRange()) {
        return;
      }

      // 异步获取远程数据（ajax）
      this.page = page;
      orderInfoAPI
        .getPageList(this.page, this.limit, this.searchObj)
        .then((response) => {
          this.list = response.data.records;
          this.total = response.data.total;
          // 数据加载并绑定成功
          this.listLoading = false;
        });
    },

    // 验证开始时间和结束时间的合法性
    validateDateRange() {
      if (
        this.searchObj.createTimeBegin &&
        this.searchObj.createTimeEnd &&
        this.searchObj.createTimeBegin > this.searchObj.createTimeEnd
      ) {
        this.$message.error("开始时间不能晚于结束时间");
        return false;
      }
      return true;
    },

    // 重置查询表单
    resetData() {
      console.log("重置查询表单");
      this.searchObj = {};
      this.fetchData();
    },
  },
};
</script>
