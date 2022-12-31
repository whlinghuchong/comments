package com.comment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comment.entity.TbShop;
import com.comment.mapper.TbShopMapper;
import com.comment.service.TbShopService;
import com.comment.vo.NearbyShopVO;
import com.comment.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.comment.consts.ShopConst.*;

/**
 * @author Administrator
 * @description 针对表【tb_shop】的数据库操作Service实现
 * @createDate 2022-12-23 19:09:54
 */
@Service
@Slf4j
public class TbShopServiceImpl extends ServiceImpl<TbShopMapper, TbShop>
        implements TbShopService {

    @Resource
    TbShopService tbShopService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    TbShopMapper tbShopMapper;

    @Override
    public String shopList(Integer page) {
        int pageNumber = 10;
        int shopCount;
        final String shopContOne = stringRedisTemplate.opsForValue().get(SHOP_LIST_CONT);
        if (StringUtils.hasText(shopContOne)) {
            shopCount = Integer.parseInt(shopContOne);
        } else {
            synchronized (this) {
                final String shopContTwo = stringRedisTemplate.opsForValue().get(SHOP_LIST_CONT);
                if (StringUtils.hasText(shopContTwo)) {
                    shopCount = Integer.parseInt(shopContTwo);
                } else {
                    shopCount = tbShopService.count();
                    stringRedisTemplate.opsForValue().set(SHOP_LIST_CONT, String.valueOf(shopCount), 60, TimeUnit.SECONDS);
                }
            }
        }
        if (page > shopCount / pageNumber + 1) {
            return null;
        }
        final String shopListPageOne = stringRedisTemplate.opsForValue().get(SHOP_LIST + page);
        if (!StringUtils.hasText(shopListPageOne)) {
            synchronized (this) {
                final String shopListPageTwo = stringRedisTemplate.opsForValue().get(SHOP_LIST + page);
                if (StringUtils.hasText(shopListPageTwo)) {
                    return shopListPageTwo;
                }
                final Page<TbShop> tbShopPage = new Page<>(page, pageNumber);
                final Page<TbShop> shopPageList = tbShopService.page(tbShopPage);
                final String shopJsonList = JSON.toJSONString(shopPageList);
                stringRedisTemplate.opsForValue().set(SHOP_LIST + page, shopJsonList, RandomUtil.randomInt(5, 10), TimeUnit.HOURS);
                return shopJsonList;
            }
        }
        return shopListPageOne;
    }

    @Override
    public String nearbyShop(NearbyShopVO nearbyShopVO) {
        log.info("开始获取redis附近店铺信息");
        final GeoResults<RedisGeoCommands.GeoLocation<String>> search = stringRedisTemplate.opsForGeo()
                .search(SHOP_GEO_KEY + nearbyShopVO.getY(),
                        GeoReference.fromCoordinate(new Point(nearbyShopVO.getX(), nearbyShopVO.getY())),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                                .includeDistance()
                                .limit(nearbyShopVO.getPage() * 10));
        log.info("获取店铺信息成功");

        if (CollectionUtil.isEmpty(search)) {
            return "没有查询到附近信息";
        }

        log.info("开始获取店铺id");
        final List<Long> shopIds = new ArrayList<>();
        Map<String, Distance> shopDistance = new HashMap<>(10);
        log.info("开始获取店铺距离对象");
        final List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = search.getContent();
        content.stream().skip(nearbyShopVO.getPage()).forEach(shop -> {
            final String shopId = shop.getContent().getName();
            shopIds.add(Long.valueOf(shopId));
            final Distance distance = shop.getDistance();
            shopDistance.put(shopId, distance);
        });
        log.info("获取店铺id和距离成功");

        log.info("开始查询店铺信息");
        final List<TbShop> tbShops = tbShopMapper.selectBatchIds(shopIds);
        final List<ShopVO> shopVOList = tbShops.stream().map(tbShop -> {
            final ShopVO shopVO = new ShopVO();
            shopVO.setShopName(tbShop.getName());
            shopVO.setShopType(tbShop.getTypeId());
            final Distance distance = shopDistance.get(tbShop.getId().toString());
            shopVO.setDistance(distance);
            return shopVO;
        }).collect(Collectors.toList());
        log.info("店铺信息封装成功进行返回");

        return JSON.toJSONString(shopVOList);
    }

    @PostConstruct
    private void info() {
        final List<TbShop> tbShops = tbShopMapper.selectList(null);
        Map<Long, List<TbShop>> longListMap = tbShops.stream().collect(Collectors.groupingBy(TbShop::getTypeId));
        for (Map.Entry<Long, List<TbShop>> longListEntry : longListMap.entrySet()) {
            final Long typeId = longListEntry.getKey();
            final List<TbShop> value = longListEntry.getValue();
            final List<RedisGeoCommands.GeoLocation<String>> geoLocations = new ArrayList<>(value.size());
            for (TbShop tbShop : value) {
                geoLocations
                        .add(new RedisGeoCommands.GeoLocation<>(tbShop.getId().toString(), new Point(tbShop.getX(), tbShop.getY())));
            }
            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(SHOP_GEO_KEY + typeId))) {
                stringRedisTemplate.opsForGeo().add(SHOP_GEO_KEY + typeId, geoLocations);
            }
        }
    }
}




