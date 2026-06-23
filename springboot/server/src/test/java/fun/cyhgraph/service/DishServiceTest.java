package fun.cyhgraph.service;

import fun.cyhgraph.vo.DishVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 👇 关键修改：强行指定 webEnvironment 为 RANDOM_PORT，这会拉起一个真正的轻量化服务器环境，WebSocket 就不会报错了！
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DishServiceTest {

    @Autowired
    private DishService dishService;

    @Test
    void testGetDishWithFlavorById() {
        // 记得改成你数据库里真实存在的菜品 ID
        Integer targetId = 1;

        DishVO dishVO = dishService.getDishWithFlavorById(targetId);

        Assertions.assertNotNull(dishVO, "测试失败：数据库中不存在该 ID 的菜品！");

        System.out.println("==========================================");
        System.out.println("✔ 单元测试运行成功！");
        System.out.println("查询到的菜品名称为: " + dishVO.getName());
        System.out.println("==========================================");
    }
}