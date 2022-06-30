<div class="col-sm-6">
<div class="card">
    <div class="card-body">
    {{#if eodAndSessionMonitor.eod}}
		<div class="card-header card-header-success">
	{{else}}
		<div class="card-header card-header-danger">
	{{/if}}
          <h4 class="card-title">E O D</h4>
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
<div class="card col-sm-6">
    <div class="card-body">
    {{#if eodAndSessionMonitor.bidding}}
		<div class="card-header card-header-success">
	{{else}}
		<div class="card-header card-header-danger">
	{{/if}}
          <h4 class="card-title">Auction</h4>
        </div>
    </div>
    <div class="card-footer">
      <div class="stats">
        <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
      </div>
    </div>
  </div>
</div>