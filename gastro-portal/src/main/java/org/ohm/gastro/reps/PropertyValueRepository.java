package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyValueRepository extends AltIdRepository<PropertyValueEntity> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllByPropertyAndRootValue(PropertyEntity property, Boolean rootValue, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllByTag(Tag tag, Sort sort);

    @Query("select distinct pv from PropertyValueEntity pv join pv.parents pp where pp=:parent")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllChildrenValues(@Param("parent") PropertyValueEntity parent, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllByProperty(PropertyEntity property, Sort sort);

    @Query("select distinct v from PropertyValueEntity v " +
            "                 join v.tags t " +
            "                 join t.product pr " +
            "                 join pr.catalog c " +
            "      where v.tag='ROOT' and c=:catalog and (pr.wasSetup=:wasSetup or :wasSetup is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllRootValues(@Param("catalog") CatalogEntity catalog, @Param("wasSetup") Boolean wasSetup);

}
