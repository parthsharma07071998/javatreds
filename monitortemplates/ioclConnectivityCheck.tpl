<div class="card">
                <div class="card-body">
                        <div class="card-header card-header-warning">
                        <h4 class="card-title">IOCL Connectivity Check</h4>
                        </div>
                         <div class="monitorcardData">
                                <p class="card-category">
                                <span  class="text-success"><i class="fa fa-long-arrow-up"></i> {{ioclConnectivityCheck.maxcount}}</span> Max Count 
                                </p>   
                                
                                <p class="card-category">
                                <span  class="text-success"><i class="fa fa-long-arrow-up"></i> {{ioclConnectivityCheck.minstatusupdatetime}}</span> Min Status Update Time  
                                </p>
                                  
                                <p class="card-category">
                                <span  class="text-success"><i class="fa fa-long-arrow-up"></i> {{ioclConnectivityCheck.maxstatusupdatetime}}</span> Max Status Update Time  
                                </p>
                                                
                                <p class="card-category">
                                <span  class="text-success"><i class="fa fa-long-arrow-up"></i> {{ioclConnectivityCheck.arrtimestamp}}</span>ARR Time Stamp  
                                </p>
                         </div>        
                        </div>
                        <div class="card-footer">
                        <div class="stats">
                                  <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
                        </div>
                </div>
        </div>

