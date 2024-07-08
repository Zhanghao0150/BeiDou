package org.gms.controller;

import org.gms.constants.api.ApiConstant;
import org.gms.dto.PlayerDTO;
import org.gms.dto.ResultBody;
import org.gms.dto.SubmitBody;
import org.gms.service.PlayerService;
import org.gms.service.PlayerService.PlayserDO;
import org.gms.service.PlayerService.UpdatePlayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mybatisflex.core.paginate.Page;

@RestController
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Tag(name = "/player/" + ApiConstant.LATEST)
    @Operation(summary = "获取在线玩家列表")
    @GetMapping("/" + ApiConstant.LATEST)
    public ResultBody<Page<PlayserDO>> allPalyerList(@RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "map", required = false) String map) {
        return ResultBody.success(playerService.getPlayerList(page, size, id, name, map));
    }

    @Tag(name = "/player/" + ApiConstant.LATEST)
    @Operation(summary = "修改玩家数据")
    @PostMapping("/" + ApiConstant.LATEST + "/src")
    public ResultBody<Object> src(@RequestBody SubmitBody<UpdatePlayerDTO> data) {
        playerService.updatePlayer(data.getData());
        return ResultBody.success();
    }
}
