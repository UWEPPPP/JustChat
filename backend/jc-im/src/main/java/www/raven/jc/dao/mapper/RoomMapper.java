package www.raven.jc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import www.raven.jc.entity.po.Room;

/**
 * room mapper
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface RoomMapper extends BaseMapper<Room> {

	/**
	 * select room by user id 联查
	 *
	 * @param userId user id
	 * @return {@link List}<{@link Room}>
	 */
	List<Room> selectRoomByUserId(@Param("userId") int userId);
}

