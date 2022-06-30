<div class="col-sm-6">
<div class="card">
    <div class="card-body">
    {{#if holidayCheck.trading}}
		<div class="card-header card-header-danger">
	{{else}}
		<div class="card-header card-header-success">
	{{/if}}
          <h4 class="card-title">Trading</h4>
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
    {{#if holidayCheck.clearing}}
		<div class="card-header card-header-danger">
	{{else}}
		<div class="card-header card-header-success">
	{{/if}}
          <h4 class="card-title">Clearing</h4>
        </div>
    </div>
    <div class="card-footer">
      <div class="stats">
        <i class="material-icons"></i> updated at {{currdate}} {{currtime}}
      </div>
    </div>
  </div>
</div>
