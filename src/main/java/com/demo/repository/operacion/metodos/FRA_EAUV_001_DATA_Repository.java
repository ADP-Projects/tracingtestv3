package com.demo.repository.operacion.metodos;

import com.demo.model.operacion.metodos.fra12eauv.datas.FRA_EAUV_001_DATA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FRA_EAUV_001_DATA_Repository extends JpaRepository<FRA_EAUV_001_DATA, Long>{
    FRA_EAUV_001_DATA findByIdFRAEAUVDATA(Long id);

    //List<FRA_EAT_001_DATA> findByFra_eat_001(FRA_EAT_001 fra_eat_001);
    //SELECT * FROM fra_eat_001_data WHERE idfraeat = 448;

    @Query(value = "select * from fra_eauv_001_data where idfraeauv = :idFRAEAUV", nativeQuery = true)
    List<FRA_EAUV_001_DATA> buscarTodosPorEnsayo(@Param("idFRAEAUV") Long id);
}
