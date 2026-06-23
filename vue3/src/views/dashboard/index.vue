<template>
  <div class="dashboard-container home">
    <!-- 营业数据 -->
    <Overview :overviewData="overviewData" />
    <!-- end -->
    <!-- 订单管理 -->
    <Orderview :orderviewData="orderviewData" />
    <!-- end -->
    <div class="homeMain">
      <!-- 菜品总览 -->
      <CuisineStatistics :dishesData="dishesData" />
      <!-- end -->
      <!-- 套餐总览 -->
      <SetMealStatistics :setMealData="setMealData" />
      <!-- end -->
    </div>
    <!-- 订单信息 -->
    <OrderList :order-statics="orderStatics" @getOrderListBy3Status="getOrderListBy3Status" />
    <!-- end -->
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { getBusinessDataAPI, getOrderDataAPI, getOverviewDishesAPI, getSetMealStatisticsAPI } from '@/api/dashboard'
import { getOrderListByAPI } from '@/api/order'
import Overview from './components/overview.vue'
import Orderview from './components/orderview.vue'
import CuisineStatistics from './components/dishStatistics.vue'
import SetMealStatistics from './components/setmealStatistics.vue'
import OrderList from './components/orderList.vue'

// 状态定义
const overviewData = ref<any>({})
const orderviewData = ref<any>({})
const dishesData = ref<any>({})
const setMealData = ref<any>({})
const orderStatics = ref<any>({})

// 初始化加载数据
const init = async () => {
  try {
    const businessData = await getBusinessDataAPI()
    overviewData.value = businessData.data.data

    const orderData = await getOrderDataAPI()
    orderviewData.value = orderData.data.data

    const overviewDishes = await getOverviewDishesAPI()
    dishesData.value = overviewDishes.data.data

    const setMealStatistics = await getSetMealStatisticsAPI()
    setMealData.value = setMealStatistics.data.data

    await getOrderListBy3Status()
  } catch (error) {
    console.error('初始化数据时出错: ', error)
  }
}

// 获取待处理，待派送，派送中数量
const getOrderListBy3Status = async () => {
  try {
    const res = await getOrderListByAPI()
    if (res.data.code === 0) {
      orderStatics.value = res.data.data
    } else {
      console.error(res.data.msg)
    }
  } catch (err) {
    console.error('请求出错了: ', err)
  }
}

onMounted(() => {
  init()
})
</script>


<!-- 子组件样式很多重复，所以统一写在父组件中，避免冗余 -->
<style lang="less">
li {
  list-style: none;
}

.dashboard-container {
  margin: 24px;
  min-height: calc(100vh - 48px);
  background: linear-gradient(180deg, #f8fbff 0%, #f4f7fa 100%);
  border-radius: 16px;

  .container {
    position: relative;
    z-index: 1;
    background: #fff;
    padding: 24px;
    border-radius: 16px;
    box-shadow:
      0 4px 20px rgba(0, 0, 0, .04),
      0 1px 2px rgba(0, 0, 0, .05);
    transition: all .3s ease;
    animation: fadeInUp .5s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow:
        0 10px 30px rgba(0, 0, 0, .08),
        0 2px 6px rgba(0, 0, 0, .05);
    }
  }

  &.home {
    .container {
      margin-bottom: 20px;
    }

    .top10 {
      padding-bottom: 0;
    }
  }
}

.homeTitle {
  font-weight: 700;
  font-size: 18px;
  color: #1f2937;
  letter-spacing: -0.2px;
  margin-bottom: 22px;
  position: relative;
  padding-left: 14px;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 4px;
    width: 4px;
    height: 18px;
    border-radius: 2px;
    background: #409eff;
  }

  i {
    font-size: 14px;
    color: #6b7280;
    padding-left: 10px;
    font-style: normal;
    font-weight: normal;
  }

  .more {
    display: flex;
    align-items: center;
    float: right;
    color: #666;
    font-size: 14px;
    font-weight: normal;
    line-height: 16px;

    a {
      display: inline-block;
      color: #666;
      text-decoration: none;
      transition: .3s;
    }

    a:hover {
      color: #409eff;
    }

    .el-icon {
      font-size: 24px;
      color: #666;
    }
  }
}

.homeMain {
  display: flex;
  gap: 20px;

  .container {
    flex: 1;

    &:last-child {
      margin-left: 0;
    }
  }
}

.overviewBox {
  ul {
    display: flex;
    gap: 20px;
    text-align: left;
    margin-left: 0;

    li {
      flex: 1;
      margin-left: 0;
      padding: 24px;

      background: linear-gradient(
        135deg,
        #409eff,
        #67c23a
      );

      border-radius: 18px;
      color: #fff;

      box-shadow:
        0 8px 20px rgba(64, 158, 255, .25);

      transition: all .3s ease;

      &:hover {
        transform: translateY(-4px);
      }
    }

    .tit {
      color: rgba(255,255,255,.9);
      font-size: 14px;
    }

    .num {
      font-size: 34px;
      font-weight: 700;
      line-height: 40px;
      padding: 14px 0;
      color: #fff;
    }

    .tip {
      color: rgba(255,255,255,.85);

      > span {
        padding-left: 5px;

        span {
          font-weight: 600;
        }
      }

      .red {
        color: #ffe6e6;
      }

      .green {
        color: #d4ffe5;
      }
    }
  }
}

.orderviewBox {
  ul {
    display: flex;
    gap: 20px;
    text-align: left;
    margin-left: 0;

    li {
      flex: 1;
      margin-left: 0;

      background: #fff;
      border: 1px solid #eef2f7;
      border-radius: 16px;
      padding: 22px;

      font-size: 14px;
      line-height: 36px;
      color: #333;

      transition: all .3s ease;

      &:hover {
        border-color: #409eff;
        box-shadow:
          0 8px 24px rgba(64, 158, 255, .15);
      }

      &.add {
        width: 120px;
        flex: none;
        background: linear-gradient(
          135deg,
          #409eff,
          #36cfc9
        );

        text-align: center;
        border: none;

        a {
          color: #fff;
          text-decoration: none;
        }

        .el-icon {
          font-size: 28px;
          margin-bottom: 8px;
          color: #fff;
        }
      }
    }

    .status {
      display: inline-flex;
      align-items: center;
      vertical-align: middle;

      .el-icon {
        font-size: 24px;
        margin-right: 10px;
        color: #409eff;
      }
    }

    .num {
      float: right;
      font-size: 34px;
      font-weight: 700;
      color: #409eff;

      a {
        color: #409eff;
        text-decoration: none;
      }
    }

    .tip {
      color: #999;

      a {
        color: #f56c6c;
        text-decoration: none;
      }
    }
  }

  .iconfont {
    font-size: 28px;
  }
}

.conTab {
  float: right;
  display: flex;
  width: 240px;
  height: 38px;
  line-height: 38px;

  overflow: hidden;

  background: #f3f6fa;
  border-radius: 12px;

  box-shadow:
    inset 0 0 0 1px #e5e7eb;

  li {
    flex: 1;
    text-align: center;
    cursor: pointer;
    transition: all .3s ease;

    &.active {
      background: #409eff;
      color: #fff;
      font-weight: 600;
    }
  }

  .el-badge__content.is-fixed {
    top: 16px;
    right: 2px;
    width: 18px;
    height: 18px;
    line-height: 16px;
    font-size: 10px;
    border-radius: 50%;
    padding: 0;
  }

  .badgeW {
    .el-badge__content.is-fixed {
      width: 30px;
      border-radius: 20px;
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
