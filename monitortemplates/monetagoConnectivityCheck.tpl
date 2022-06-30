<div class="col-sm-6">
<div class="card">
    <div class="card-body">
   	{{#if monetagoConnectivityCheck.monetagoConnectivityFlag}}
		<div class="card-header card-header-success">
	{{else}}
		<div class="card-header card-header-danger">
	{{/if}}
          <h4 class="card-title">Monetago</h4>
        </div>
    </div>
    <div class="card-footer">
      <div class="stats">
        <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
      </div>
    </div>
  </div>
</div>
<div class="col-sm-6">
<div class="card">
    <div class="card-body">
   	{{#if monetagoConnectivityCheck.instAuthConnectivityFlag}}
		<div class="card-header card-header-success">
	{{else}}
		<div class="card-header card-header-danger">
	{{/if}}
          <h4 class="card-title">Invoice Auth</h4>
        </div>
    </div>
    <div class="card-footer">
      <div class="stats">
        <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
      </div>
    </div>
  </div>
</div>