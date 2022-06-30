       <div class="card">
    <div class="card-body">
      <div class="card-header card-header-warning">
          <h4 class="card-title">Created Today</h4>
        </div>
      <div class="monitorcardData">
              <p class="card-category">
                <span class="text-success"><i class="fa fa-file"></i> {{instrumentMonitor.instrumentsCreatedToday}} </span> Instruments
              </p>
              <p class="card-category">
                <span class="text-success"><i class="fa fa-files-o"></i> {{instrumentMonitor.groupInstsCreatedToday}} </span>  Group Instruments
              </p>
              <p class="card-category">
                <span class="text-success"><i class="fa fa-gavel"></i> {{instrumentMonitor.factoringUnitsCreatedToday}} </span> Factoring units
              </p>
              <p class="card-category">
                <span class="text-success"><i class="fa fa-gavel"></i> {{instrumentMonitor.factoringUnitsFactoredToday}} </span> Instruments Factored
              </p>
      </div>
    </div>
    <div class="card-footer">
      <div class="stats">
        <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
      </div>
    </div>
  </div>
