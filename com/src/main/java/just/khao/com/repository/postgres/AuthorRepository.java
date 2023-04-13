package just.khao.com.repository.postgres;

import just.khao.com.entity.Author.AuthorEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorRepository {
    public void registerAuthor(AuthorEntity authorEntity);
}
